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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitizationServiceTest {

    @Mock
    private DigitizationRepository digitizationRepository;

    @Mock
    private PieceRepository pieceRepository;

    @InjectMocks
    private DigitizationService digitizationService;

    private Piece piece;
    private Digitization digitization;

    @BeforeEach
    void setUp() {
        piece = new Piece();
        piece.setId(1L);
        piece.setName("Fotografia historica");

        digitization = new Digitization();
        digitization.setId(1L);
        digitization.setUrl("https://archivo.com/foto.jpg");
        digitization.setDescription("Fotografia digitalizada");
        digitization.setPiece(piece);
    }

    @Test
    void createDigitization() {
        DigitizationCreateRequestDTO request =
                new DigitizationCreateRequestDTO(
                        digitization.getUrl(),
                        digitization.getDescription(),
                        piece.getId());

        when(pieceRepository.findById(1L))
                .thenReturn(Optional.of(piece));

        when(digitizationRepository.save(any(Digitization.class)))
                .thenAnswer(invocation -> {
                    Digitization savedDigitization = invocation.getArgument(0);
                    savedDigitization.setId(1L);
                    return savedDigitization;
                });

        DigitizationResponseDTO response =
                digitizationService.createDigitization(request);

        assertEquals(1L, response.id());
        assertEquals(request.url(), response.url());
        assertEquals(request.description(), response.description());
        assertEquals(request.pieceId(), response.pieceId());

        verify(digitizationRepository).save(any(Digitization.class));
    }

    @Test
    void listDigitizations() {
        when(digitizationRepository.findAll())
                .thenReturn(List.of(digitization));

        List<DigitizationResponseDTO> response =
                digitizationService.listDigitizations();

        assertEquals(1, response.size());
        assertEquals(digitization.getUrl(), response.get(0).url());
        assertEquals(piece.getId(), response.get(0).pieceId());
    }

    @Test
    void getDigitization() {
        when(digitizationRepository.findById(1L))
                .thenReturn(Optional.of(digitization));

        DigitizationResponseDTO response =
                digitizationService.getDigitization(1L);

        assertEquals(digitization.getId(), response.id());
        assertEquals(digitization.getUrl(), response.url());
        assertEquals(piece.getId(), response.pieceId());
    }

    @Test
    void getDigitizationNotFound() {
        when(digitizationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                DigitizationNotFoundException.class,
                () -> digitizationService.getDigitization(99L));
    }

    @Test
    void createDigitizationWithPieceNotFound() {
        DigitizationCreateRequestDTO request =
                new DigitizationCreateRequestDTO(
                        digitization.getUrl(),
                        digitization.getDescription(),
                        99L);

        when(pieceRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PieceNotFoundException.class,
                () -> digitizationService.createDigitization(request));

        verify(digitizationRepository, never())
                .save(any(Digitization.class));
    }
}