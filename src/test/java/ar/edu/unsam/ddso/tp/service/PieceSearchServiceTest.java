package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PieceSearchServiceTest {

    @Mock
    private PieceRepository pieceRepository;

    private PieceSearchService pieceSearchService;

    @BeforeEach
    void setUp() {
        pieceSearchService = new PieceSearchService(pieceRepository);
    }

    @Test
    void searchPiecesNormalizesBlankStringsAndMapsResults() {
        // Given
        LocalDate date = LocalDate.of(1920, 5, 15);
        Piece piece = new Piece(1L, "Historic Photo", "Archive photograph", date);
        when(pieceRepository.searchPieces("Photo", null, date, "foto"))
                .thenReturn(List.of(piece));

        // When
        List<PieceResponseDTO> result =
                pieceSearchService.searchPieces("  Photo  ", "   ", date, " foto ");

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Historic Photo", result.get(0).name());
        assertEquals("Archive photograph", result.get(0).description());
        assertEquals(date, result.get(0).date());
        verify(pieceRepository).searchPieces("Photo", null, date, "foto");
    }

    @Test
    void searchPiecesWithAllNullFiltersDelegatesAllNulls() {
        // Given
        Piece first = new Piece(1L, "Piece One", "First description", LocalDate.of(2000, 1, 10));
        Piece second = new Piece(2L, "Piece Two", "Second description", LocalDate.of(2001, 2, 20));
        when(pieceRepository.searchPieces(null, null, null, null))
                .thenReturn(List.of(first, second));

        // When
        List<PieceResponseDTO> result = pieceSearchService.searchPieces(null, null, null, null);

        // Then
        assertEquals(2, result.size());
        assertEquals("Piece One", result.get(0).name());
        assertEquals("Piece Two", result.get(1).name());
        verify(pieceRepository).searchPieces(null, null, null, null);
    }

    @Test
    void searchPiecesByDescriptionOnlyPassesNormalizedDescription() {
        // Given
        Piece piece = new Piece(3L, "Map", "Old city map", LocalDate.of(1895, 8, 3));
        when(pieceRepository.searchPieces(null, "city map", null, null))
                .thenReturn(List.of(piece));

        // When
        List<PieceResponseDTO> result =
                pieceSearchService.searchPieces("", " city map ", null, "");

        // Then
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).id());
        verify(pieceRepository).searchPieces(null, "city map", null, null);
    }
}