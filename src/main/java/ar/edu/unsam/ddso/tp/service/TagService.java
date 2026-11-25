package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.TagCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.exception.TagNotFoundException;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.model.exceptions.DuplicateTagNameException;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // ---------- Consultas ----------

    public List<TagResponseDTO> listTags(Boolean activeOnly) {
        List<Tag> tags =
                Boolean.TRUE.equals(activeOnly)
                        ? tagRepository.findByActiveTrue()
                        : tagRepository.findAll();
        return tags.stream().map(this::toResponse).toList();
    }

    public TagResponseDTO getTag(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ---------- Operaciones ----------

    @Transactional
    public TagResponseDTO createTag(TagCreateRequestDTO request) {
        if (tagRepository.existsByName(request.name())) {
            throw new DuplicateTagNameException(request.name());
        }
        Tag tag = new Tag();
        tag.setName(request.name());
        tag.setActive(true);
        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public TagResponseDTO activateTag(Long id) {
        Tag tag = findOrThrow(id);
        tag.activate();
        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public TagResponseDTO deactivateTag(Long id) {
        Tag tag = findOrThrow(id);
        tag.deactivate();
        return toResponse(tagRepository.save(tag));
    }

    // ---------- Privados ----------

    private Tag findOrThrow(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    private TagResponseDTO toResponse(Tag tag) {
        return new TagResponseDTO(tag.getId(), tag.getName(), tag.isActive());
    }
}
