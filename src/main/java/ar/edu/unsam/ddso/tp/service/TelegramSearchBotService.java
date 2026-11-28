package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TelegramSearchBotService {

    static final int TELEGRAM_MAX_MESSAGE_LENGTH = 4096;
    private static final String TRUNCATION_NOTICE =
            "\n\n... (resultados truncados por límite de Telegram)";

    private final PieceSearchService pieceSearchService;

    public TelegramSearchBotService(PieceSearchService pieceSearchService) {
        this.pieceSearchService = pieceSearchService;
    }

    // ---------- Consultas ----------

    public String handleMessage(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return helpMessage();
        }

        String text = rawText.trim();
        if (text.startsWith("/start") || text.startsWith("/ayuda") || text.startsWith("/help")) {
            return helpMessage();
        }

        String queryBody = text.startsWith("/buscar")
                ? text.substring("/buscar".length()).trim()
                : text;

        if (queryBody.isBlank()) {
            return helpMessage();
        }

        ParseResult parseResult = parseFilters(queryBody);
        if (parseResult.errorMessage().isPresent()) {
            return parseResult.errorMessage().get();
        }

        SearchFilters filters = parseResult.filters();
        List<PieceResponseDTO> pieces = pieceSearchService.searchPieces(
                filters.name(),
                filters.description(),
                filters.date(),
                filters.tag());

        if (pieces.isEmpty()) {
            return "No se encontraron piezas con ese criterio de búsqueda.";
        }

        return formatResponse(pieces);
    }

    // ---------- Privados ----------

    private String formatResponse(List<PieceResponseDTO> pieces) {
        StringBuilder response = new StringBuilder("Piezas encontradas (" + pieces.size() + "):\n");
        for (PieceResponseDTO piece : pieces) {
            StringBuilder entry = new StringBuilder();
            entry.append("\n• #")
                    .append(piece.id())
                    .append(" - ")
                    .append(piece.name());
            if (piece.date() != null) {
                entry.append(" (").append(piece.date()).append(")");
            }
            if (piece.description() != null && !piece.description().isBlank()) {
                entry.append("\n  ").append(piece.description());
            }

            if (response.length() + entry.length() + TRUNCATION_NOTICE.length()
                    > TELEGRAM_MAX_MESSAGE_LENGTH) {
                response.append(TRUNCATION_NOTICE);
                break;
            }
            response.append(entry);
        }

        if (response.length() > TELEGRAM_MAX_MESSAGE_LENGTH) {
            int maxBody = TELEGRAM_MAX_MESSAGE_LENGTH - TRUNCATION_NOTICE.length();
            return response.substring(0, maxBody) + TRUNCATION_NOTICE;
        }
        return response.toString();
    }

    private ParseResult parseFilters(String input) {
        if (!input.contains("=") && !input.contains("|")) {
            return ParseResult.ok(new SearchFilters(input, null, null, null));
        }

        String name = null;
        String description = null;
        LocalDate date = null;
        String tag = null;
        boolean hasValidFilter = false;

        String[] parts = input.split("\\|");
        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                return ParseResult.error(
                        "Filtro vacío. Usá el formato clave=valor separados por |.");
            }

            int separatorIndex = trimmed.indexOf('=');
            if (separatorIndex <= 0) {
                return ParseResult.error(
                        "Formato de filtro inválido: '" + trimmed + "'. Usá clave=valor.");
            }

            String key = trimmed.substring(0, separatorIndex).trim().toLowerCase();
            String value = trimmed.substring(separatorIndex + 1).trim();
            if (value.isBlank()) {
                return ParseResult.error("El filtro '" + key + "' no puede estar vacío.");
            }

            switch (key) {
                case "nombre", "name" -> {
                    name = value;
                    hasValidFilter = true;
                }
                case "descripcion", "description" -> {
                    description = value;
                    hasValidFilter = true;
                }
                case "fecha", "date" -> {
                    try {
                        date = LocalDate.parse(value);
                        hasValidFilter = true;
                    } catch (DateTimeParseException ex) {
                        return ParseResult.error(
                                "Formato de fecha inválido. Usá AAAA-MM-DD (ejemplo: fecha=1920-05-15).");
                    }
                }
                case "tag", "etiqueta" -> {
                    tag = value;
                    hasValidFilter = true;
                }
                default -> {
                    return ParseResult.error(
                            "Filtro desconocido: '" + key
                                    + "'. Claves válidas: nombre, descripcion, fecha, tag.");
                }
            }
        }

        if (!hasValidFilter) {
            return ParseResult.error(
                    "Debés indicar al menos un filtro válido (nombre, descripcion, fecha o tag).");
        }

        return ParseResult.ok(new SearchFilters(name, description, date, tag));
    }

    private String helpMessage() {
        return """
                Buscador del Archivo Digital:
                • Escribí un nombre o palabra clave (ej: foto)
                • O usá filtros separados por | :
                  /buscar nombre=foto | tag=archivo | fecha=1920-05-15""";
    }

    private record SearchFilters(
            String name,
            String description,
            LocalDate date,
            String tag) {
    }

    private record ParseResult(SearchFilters filters, Optional<String> errorMessage) {
        static ParseResult ok(SearchFilters filters) {
            return new ParseResult(filters, Optional.empty());
        }

        static ParseResult error(String message) {
            return new ParseResult(null, Optional.of(message));
        }
    }
}