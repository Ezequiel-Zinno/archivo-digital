package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.CatalogingCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.CatalogingResponseDTO;
import ar.edu.unsam.ddso.tp.exception.CatalogingNotFoundException;
import ar.edu.unsam.ddso.tp.exception.DigitizationNotFoundException;
import ar.edu.unsam.ddso.tp.exception.TagNotFoundException;
import ar.edu.unsam.ddso.tp.model.Cataloging;
import ar.edu.unsam.ddso.tp.model.Digitization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.model.exceptions.InactiveTagException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagAlreadyCatalogingedException;
import ar.edu.unsam.ddso.tp.repository.CatalogingRepository;
import ar.edu.unsam.ddso.tp.repository.DigitizationRepository;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogingServiceTest {

    @Mock private CatalogingRepository catalogingRepository;
    @Mock private DigitizationRepository digitizationRepository;
    @Mock private TagRepository tagRepository;

    private CatalogingService service() {
        return new CatalogingService(
                catalogingRepository, digitizationRepository, tagRepository);
    }

    private Digitization digitization(Long id) {
        Piece piece = new Piece(id, "Pieza 1", "Descripcion", LocalDate.now());
        return new Digitization(id, "http://example.com/1", "Digitalizacion 1", piece, null);
    }

    private Tag tag(Long id, String name, boolean active) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setActive(active);
        return tag;
    }

    @Test
    void catalogAssociatesActiveTag() {
        // Given
        CatalogingService service = service();
        Digitization digitization = digitization(1L);
        Tag tag = tag(2L, "foto", true);
        when(digitizationRepository.findById(1L)).thenReturn(Optional.of(digitization));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));
        when(catalogingRepository.existsByDigitizationIdAndTagId(1L, 2L)).thenReturn(false);
        when(catalogingRepository.save(any(Cataloging.class)))
                .thenAnswer(
                        invocation -> {
                            Cataloging saved = invocation.getArgument(0);
                            saved.setId(10L);
                            return saved;
                        });

        // When
        CatalogingResponseDTO response =
                service.catalog(1L, new CatalogingCreateRequestDTO(2L));

        // Then
        assertEquals(10L, response.id());
        assertEquals(1L, response.digitizationId());
        assertEquals(2L, response.tag().id());
    }

    @Test
    void catalogWithUnknownDigitizationThrowsNotFound() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.findById(404L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                DigitizationNotFoundException.class,
                () -> service.catalog(404L, new CatalogingCreateRequestDTO(2L)));
    }

    @Test
    void catalogWithUnknownTagThrowsNotFound() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.findById(1L)).thenReturn(Optional.of(digitization(1L)));
        when(tagRepository.findById(404L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                TagNotFoundException.class,
                () -> service.catalog(1L, new CatalogingCreateRequestDTO(404L)));
    }

    @Test
    void catalogInactiveTagThrows() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.findById(1L)).thenReturn(Optional.of(digitization(1L)));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag(2L, "borrador", false)));

        // When / Then
        assertThrows(
                InactiveTagException.class,
                () -> service.catalog(1L, new CatalogingCreateRequestDTO(2L)));
    }

    @Test
    void catalogRepeatedTagThrowsConflict() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.findById(1L)).thenReturn(Optional.of(digitization(1L)));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag(2L, "foto", true)));
        when(catalogingRepository.existsByDigitizationIdAndTagId(1L, 2L)).thenReturn(true);

        // When / Then
        assertThrows(
                TagAlreadyCatalogingedException.class,
                () -> service.catalog(1L, new CatalogingCreateRequestDTO(2L)));
    }

    @Test
    void tagsOfDigitizationReturnsMappedTags() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.existsById(1L)).thenReturn(true);
        Digitization digitization = digitization(1L);
        Tag tag = tag(2L, "foto", true);
        Cataloging cataloging = new Cataloging(10L, digitization, tag);
        when(catalogingRepository.findByDigitizationId(1L)).thenReturn(List.of(cataloging));

        // When
        List<CatalogingResponseDTO> response = service.tagsOfDigitization(1L);

        // Then
        assertEquals(1, response.size());
        assertEquals("foto", response.get(0).tag().name());
    }

    @Test
    void uncatalogRemovesAssociation() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.existsById(1L)).thenReturn(true);
        when(tagRepository.existsById(2L)).thenReturn(true);
        when(catalogingRepository.existsByDigitizationIdAndTagId(1L, 2L)).thenReturn(true);

        // When
        service.uncatalog(1L, 2L);

        // Then
        verify(catalogingRepository).deleteByDigitizationIdAndTagId(1L, 2L);
    }

    @Test
    void uncatalogMissingAssociationThrowsNotFound() {
        // Given
        CatalogingService service = service();
        when(digitizationRepository.existsById(1L)).thenReturn(true);
        when(tagRepository.existsById(2L)).thenReturn(true);
        when(catalogingRepository.existsByDigitizationIdAndTagId(1L, 2L)).thenReturn(false);

        // When / Then
        assertThrows(CatalogingNotFoundException.class, () -> service.uncatalog(1L, 2L));
    }
}
