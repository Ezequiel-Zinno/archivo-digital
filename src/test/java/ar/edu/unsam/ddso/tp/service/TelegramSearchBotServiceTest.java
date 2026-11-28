package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramSearchBotServiceTest {

    @Mock
    private PieceSearchService pieceSearchService;

    private TelegramSearchBotService botService;

    @BeforeEach
    void setUp() {
        botService = new TelegramSearchBotService(pieceSearchService);
    }

    @Test
    void handleMessageWithStartReturnsHelp() {
        String reply = botService.handleMessage("/start");
        assertTrue(reply.contains("Buscador del Archivo Digital"));
    }

    @Test
    void handleMessageWithFiltersCallsSearchServiceAndFormatsResults() {
        LocalDate date = LocalDate.of(1920, 5, 15);
        PieceResponseDTO dto = new PieceResponseDTO(1L, "Foto histórica", "Archivo", date);
        when(pieceSearchService.searchPieces("foto", null, date, "archivo"))
                .thenReturn(List.of(dto));

        String reply = botService.handleMessage("/buscar nombre=foto | tag=archivo | fecha=1920-05-15");

        assertTrue(reply.contains("#1 - Foto histórica"));
        assertTrue(reply.contains("1920-05-15"));
        verify(pieceSearchService).searchPieces("foto", null, date, "archivo");
    }

    @Test
    void handleMessageWhenNoPiecesFoundReturnsFriendlyMessage() {
        when(pieceSearchService.searchPieces("inexistente", null, null, null))
                .thenReturn(List.of());

        String reply = botService.handleMessage("inexistente");

        assertTrue(reply.contains("No se encontraron piezas"));
    }

    @Test
    void handleMessageWithUnknownFilterReturnsValidationError() {
        String reply = botService.handleMessage("/buscar autor=Perez");

        assertTrue(reply.contains("Filtro desconocido"));
        verifyNoInteractions(pieceSearchService);
    }

    @Test
    void handleMessageWithEmptyFilterValueReturnsValidationError() {
        String reply = botService.handleMessage("/buscar nombre=");

        assertTrue(reply.contains("no puede estar vacío"));
        verifyNoInteractions(pieceSearchService);
    }

    @Test
    void handleMessageWithMalformedFilterReturnsValidationError() {
        String reply = botService.handleMessage("/buscar nombre=foto | sinigual");

        assertTrue(reply.contains("Formato de filtro inválido"));
        verifyNoInteractions(pieceSearchService);
    }

    @Test
    void handleMessageWithInvalidDateReturnsValidationError() {
        String reply = botService.handleMessage("/buscar fecha=15-05-1920");

        assertTrue(reply.contains("Formato de fecha inválido"));
        verifyNoInteractions(pieceSearchService);
    }

    @Test
    void handleMessageTruncatesResponseWhenExceedingTelegramLimit() {
        List<PieceResponseDTO> manyPieces = new ArrayList<>();
        String longDescription = "Descripcion larga ".repeat(20);
        for (long i = 1; i <= 30; i++) {
            manyPieces.add(new PieceResponseDTO(i, "Pieza número " + i, longDescription, LocalDate.of(2020, 1, 1)));
        }
        when(pieceSearchService.searchPieces("pieza", null, null, null)).thenReturn(manyPieces);

        String reply = botService.handleMessage("pieza");

        assertTrue(reply.length() <= TelegramSearchBotService.TELEGRAM_MAX_MESSAGE_LENGTH);
        assertTrue(reply.contains("truncados"));
    }
}