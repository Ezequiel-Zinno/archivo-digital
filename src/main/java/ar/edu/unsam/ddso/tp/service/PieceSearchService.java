package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PieceSearchService {

    private final PieceRepository pieceRepository;

    public PieceSearchService(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    // ---------- Consultas ----------

    public List<PieceResponseDTO> searchPieces(
            String name, String description, LocalDate date, String tag) {
        String normalizedName = normalize(name);
        String normalizedDescription = normalize(description);
        String normalizedTag = normalize(tag);

        return pieceRepository
                .searchPieces(normalizedName, normalizedDescription, date, normalizedTag)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ---------- Privados ----------

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private PieceResponseDTO toResponse(Piece piece) {
        return new PieceResponseDTO(
                piece.getId(),
                piece.getName(),
                piece.getDescription(),
                piece.getDate());
    }
}