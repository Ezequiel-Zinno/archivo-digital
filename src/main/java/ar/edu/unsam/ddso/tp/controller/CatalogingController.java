package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.CatalogingCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.CatalogingResponseDTO;
import ar.edu.unsam.ddso.tp.service.CatalogingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/digitizations/{digitizationId}/catalogings")
public class CatalogingController {

    private final CatalogingService catalogingService;

    public CatalogingController(CatalogingService catalogingService) {
        this.catalogingService = catalogingService;
    }

    // ---------- Consultas ----------

    @GetMapping
    public List<CatalogingResponseDTO> tagsOfDigitization(@PathVariable Long digitizationId) {
        return catalogingService.tagsOfDigitization(digitizationId);
    }

    // ---------- Operaciones ----------

    @PostMapping
    public ResponseEntity<CatalogingResponseDTO> catalog(
            @PathVariable Long digitizationId,
            @Valid @RequestBody CatalogingCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogingService.catalog(digitizationId, request));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> uncatalog(
            @PathVariable Long digitizationId, @PathVariable Long tagId) {
        catalogingService.uncatalog(digitizationId, tagId);
        return ResponseEntity.noContent().build();
    }
}
