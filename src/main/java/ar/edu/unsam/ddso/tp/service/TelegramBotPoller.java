package ar.edu.unsam.ddso.tp.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
public class TelegramBotPoller {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotPoller.class);
    private static final String TELEGRAM_API_BASE = "https://api.telegram.org/bot";

    private final TelegramSearchBotService botService;
    private final RestClient restClient;
    private final String botToken;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private long lastUpdateId = 0;

    public TelegramBotPoller(
            TelegramSearchBotService botService,
            @Value("${telegram.bot.token:}") String botToken) {
        this.botService = botService;
        this.restClient = RestClient.builder().build();
        this.botToken = botToken == null ? "" : botToken.trim();
    }

    // ---------- Eventos ----------

    @EventListener(ApplicationReadyEvent.class)
    public void startPolling() {
        if (botToken.isBlank()) {
            LOGGER.info("TELEGRAM_BOT_TOKEN no configurado; el bot de Telegram no iniciará.");
            return;
        }
        LOGGER.info("Iniciando polling del bot de Telegram...");
        scheduler.scheduleWithFixedDelay(this::pollUpdates, 0, 2, TimeUnit.SECONDS);
    }

    // ---------- Privados ----------

    private void pollUpdates() {
        try {
            long offset = lastUpdateId + 1;
            String url = TELEGRAM_API_BASE + botToken + "/getUpdates?timeout=1&offset=" + offset;
            JsonNode root = restClient.get().uri(url).retrieve().body(JsonNode.class);
            if (root == null || !root.path("ok").asBoolean(false)) {
                return;
            }

            JsonNode result = root.path("result");
            if (!result.isArray()) {
                return;
            }

            for (JsonNode update : result) {
                long updateId = update.path("update_id").asLong(0);
                if (updateId > lastUpdateId) {
                    lastUpdateId = updateId;
                }

                JsonNode message = update.path("message");
                if (message.isMissingNode()) {
                    continue;
                }

                String text = message.path("text").asText(null);
                long chatId = message.path("chat").path("id").asLong(0);
                if (text != null && chatId != 0) {
                    String reply = botService.handleMessage(text);
                    sendMessage(chatId, reply);
                }
            }
        } catch (Exception ex) {
            LOGGER.warn("Error consultando actualizaciones de Telegram: {}", ex.getMessage());
        }
    }

    private void sendMessage(long chatId, String text) {
        String url = TELEGRAM_API_BASE + botToken + "/sendMessage";
        SendMessagePayload payload = new SendMessagePayload(chatId, text);
        restClient.post().uri(url).body(payload).retrieve().toBodilessEntity();
    }

    private record SendMessagePayload(long chat_id, String text) {
    }
}