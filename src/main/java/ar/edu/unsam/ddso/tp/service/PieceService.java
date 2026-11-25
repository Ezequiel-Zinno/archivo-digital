package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.exception.PieceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PieceService {

    private final PieceRepository pieceRepository;

    public PieceService(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    public List<PieceResponseDTO> listPieces() {
        return pieceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PieceResponseDTO getPiece(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public PieceResponseDTO createPiece(PieceCreateRequestDTO request) {
        Piece piece = new Piece();
        piece.setName(request.name());
        piece.setDescription(request.description());
        piece.setDate(request.date());
        return toResponse(pieceRepository.save(piece));
    }

    private Piece findOrThrow(Long id) {
        return pieceRepository.findById(id)
                .orElseThrow(() -> new PieceNotFoundException(id));
    }

    private PieceResponseDTO toResponse(Piece piece) {
        return new PieceResponseDTO(
                piece.getId(),
                piece.getName(),
                piece.getDescription(),
                piece.getDate());
    }
}