package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.unsam.ddso.tp.dto.TagCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.exception.TagNotFoundException;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.model.exceptions.DuplicateTagNameException;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository tagRepository;

    private TagService tagService;

    private TagService service() {
        tagService = new TagService(tagRepository);
        return tagService;
    }

    private Tag tag(Long id, String name, boolean active) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setActive(active);
        return tag;
    }

    // Given / When / Then tests for create, get, activate, deactivate, duplicate

    @Test
    void createTagPersistsNewActiveTag() {
        // Given
        TagService service = service();
        when(tagRepository.existsByName("archivo")).thenReturn(false);
        when(tagRepository.save(org.mockito.ArgumentMatchers.any(Tag.class)))
                .thenAnswer(
                        invocation -> {
                            Tag saved = invocation.getArgument(0);
                            saved.setId(1L);
                            return saved;
                        });

        // When
        TagResponseDTO response = service.createTag(new TagCreateRequestDTO("archivo"));

        // Then
        assertEquals(1L, response.id());
        assertEquals("archivo", response.name());
        assertTrue(response.active());
        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepository).save(captor.capture());
        assertTrue(captor.getValue().isActive());
    }

    @Test
    void createTagWithDuplicatedNameThrows() {
        // Given
        TagService service = service();
        when(tagRepository.existsByName("archivo")).thenReturn(true);

        // When / Then
        assertThrows(
                DuplicateTagNameException.class,
                () -> service.createTag(new TagCreateRequestDTO("archivo")));
    }

    @Test
    void getTagReturnsResponse() {
        // Given
        TagService service = service();
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag(1L, "foto", true)));

        // When
        TagResponseDTO response = service.getTag(1L);

        // Then
        assertEquals("foto", response.name());
        assertTrue(response.active());
    }

    @Test
    void getTagMissingThrowsNotFound() {
        // Given
        TagService service = service();
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(TagNotFoundException.class, () -> service.getTag(99L));
    }

    @Test
    void deactivateTagMarksInactive() {
        // Given
        TagService service = service();
        Tag existing = tag(1L, "foto", true);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(existing);

        // When
        TagResponseDTO response = service.deactivateTag(1L);

        // Then
        assertFalse(response.active());
    }

    @Test
    void activateTagMarksActive() {
        // Given
        TagService service = service();
        Tag existing = tag(1L, "foto", false);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(tagRepository.save(existing)).thenReturn(existing);

        // When
        TagResponseDTO response = service.activateTag(1L);

        // Then
        assertTrue(response.active());
    }

    @Test
    void listActiveTagsUsesDerivedQuery() {
        // Given
        TagService service = service();
        when(tagRepository.findByActiveTrue()).thenReturn(List.of(tag(1L, "foto", true)));

        // When
        List<TagResponseDTO> tags = service.listTags(true);

        // Then
        assertEquals(1, tags.size());
        assertEquals("foto", tags.get(0).name());
    }
}
