package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.TagCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.service.TagService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // ---------- Consultas ----------

    @GetMapping
    public List<TagResponseDTO> listTags(
            @RequestParam(name = "active", required = false) Boolean active) {
        return tagService.listTags(active);
    }

    @GetMapping("/{id}")
    public TagResponseDTO getTag(@PathVariable Long id) {
        return tagService.getTag(id);
    }

    // ---------- Operaciones ----------

    @PostMapping
    public ResponseEntity<TagResponseDTO> createTag(
            @Valid @RequestBody TagCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(request));
    }

    @PostMapping("/{id}/activate")
    public TagResponseDTO activateTag(@PathVariable Long id) {
        return tagService.activateTag(id);
    }

    @PostMapping("/{id}/deactivate")
    public TagResponseDTO deactivateTag(@PathVariable Long id) {
        return tagService.deactivateTag(id);
    }
}
