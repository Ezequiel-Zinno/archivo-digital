package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.DigitizationCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.exception.DigitizationNotFoundException;
import ar.edu.unsam.ddso.tp.exception.PieceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DigitizationService {

    private final DigitizationRepository digitizationRepository;
    private final PieceRepository pieceRepository;

    public DigitizationService(
            DigitizationRepository digitizationRepository,
            PieceRepository pieceRepository) {
        this.digitizationRepository = digitizationRepository;
        this.pieceRepository = pieceRepository;
    }

    public List<DigitizationResponseDTO> listDigitizations() {
        return digitizationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public DigitizationResponseDTO getDigitization(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public DigitizationResponseDTO createDigitization(
            DigitizationCreateRequestDTO request) {
        Piece piece = pieceRepository.findById(request.pieceId())
                .orElseThrow(() -> new PieceNotFoundException(request.pieceId()));

        Digitization digitization = new Digitization();
        digitization.setUrl(request.url());
        digitization.setDescription(request.description());
        digitization.setPiece(piece);

        return toResponse(digitizationRepository.save(digitization));
    }

    private Digitization findOrThrow(Long id) {
        return digitizationRepository.findById(id)
                .orElseThrow(() -> new DigitizationNotFoundException(id));
    }

    private DigitizationResponseDTO toResponse(Digitization digitization) {
        return new DigitizationResponseDTO(
                digitization.getId(),
                digitization.getUrl(),
                digitization.getDescription(),
                digitization.getPiece().getId());
    }
}