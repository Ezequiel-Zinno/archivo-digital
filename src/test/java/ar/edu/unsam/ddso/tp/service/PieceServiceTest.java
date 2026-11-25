package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.exception.PieceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PieceServiceTest {

    @Mock
    private PieceRepository pieceRepository;

    @InjectMocks
    private PieceService pieceService;

    private Piece piece;

    @BeforeEach
    void setUp() {
        piece = new Piece();
        piece.setId(1L);
        piece.setName("Fotografia historica");
        piece.setDescription("Fotografia del archivo");
        piece.setDate(LocalDate.of(1980, 5, 10));
    }

    @Test
    void createPiece() {
        PieceCreateRequestDTO request = new PieceCreateRequestDTO(
                piece.getName(),
                piece.getDescription(),
                piece.getDate());

        when(pieceRepository.save(any(Piece.class))).thenAnswer(invocation -> {
            Piece savedPiece = invocation.getArgument(0);
            savedPiece.setId(1L);
            return savedPiece;
        });

        PieceResponseDTO response = pieceService.createPiece(request);

        assertEquals(1L, response.id());
        assertEquals(request.name(), response.name());
        assertEquals(request.description(), response.description());
        assertEquals(request.date(), response.date());

        verify(pieceRepository).save(any(Piece.class));
    }

    @Test
    void listPieces() {
        when(pieceRepository.findAll()).thenReturn(List.of(piece));

        List<PieceResponseDTO> response = pieceService.listPieces();

        assertEquals(1, response.size());
        assertEquals(piece.getName(), response.get(0).name());
    }

    @Test
    void getPiece() {
        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));

        PieceResponseDTO response = pieceService.getPiece(1L);

        assertEquals(piece.getId(), response.id());
        assertEquals(piece.getName(), response.name());
    }

    @Test
    void getPieceNotFound() {
        when(pieceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                PieceNotFoundException.class,
                () -> pieceService.getPiece(99L));
    }
}