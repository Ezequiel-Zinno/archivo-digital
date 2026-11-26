package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TelegramSearchBotService {

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

        SearchFilters filters = parseFilters(queryBody);
        if (filters.invalidDate()) {
            return "Formato de fecha inválido. Usá AAAA-MM-DD (ejemplo: fecha=1920-05-15).";
        }

        List<PieceResponseDTO> pieces = pieceSearchService.searchPieces(
                filters.name(),
                filters.description(),
                filters.date(),
                filters.tag());

        if (pieces.isEmpty()) {
            return "No se encontraron piezas con ese criterio de búsqueda.";
        }

        StringBuilder response = new StringBuilder("Piezas encontradas (" + pieces.size() + "):\n");
        for (PieceResponseDTO piece : pieces) {
            response.append("\n• #")
                    .append(piece.id())
                    .append(" - ")
                    .append(piece.name());
            if (piece.date() != null) {
                response.append(" (").append(piece.date()).append(")");
            }
            if (piece.description() != null && !piece.description().isBlank()) {
                response.append("\n  ").append(piece.description());
            }
        }
        return response.toString();
    }

    // ---------- Privados ----------

    private SearchFilters parseFilters(String input) {
        if (!input.contains("=") && !input.contains("|")) {
            return new SearchFilters(input, null, null, null, false);
        }

        String name = null;
        String description = null;
        LocalDate date = null;
        String tag = null;

        String[] parts = input.split("\\|");
        for (String part : parts) {
            String trimmed = part.trim();
            int separatorIndex = trimmed.indexOf('=');
            if (separatorIndex <= 0) {
                continue;
            }
            String key = trimmed.substring(0, separatorIndex).trim().toLowerCase();
            String value = trimmed.substring(separatorIndex + 1).trim();
            if (value.isBlank()) {
                continue;
            }

            switch (key) {
                case "nombre", "name" -> name = value;
                case "descripcion", "description" -> description = value;
                case "fecha", "date" -> {
                    try {
                        date = LocalDate.parse(value);
                    } catch (DateTimeParseException ex) {
                        return new SearchFilters(null, null, null, null, true);
                    }
                }
                case "tag", "etiqueta" -> tag = value;
                default -> {
                    // Clave desconocida ignorada
                }
            }
        }

        return new SearchFilters(name, description, date, tag, false);
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
            String tag,
            boolean invalidDate) {
    }
}