package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.DigitizationCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitizationResponseDTO;
import ar.edu.unsam.ddso.tp.service.DigitizationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/digitizations")
public class DigitizationController {

    private final DigitizationService digitizationService;

    public DigitizationController(DigitizationService digitizationService) {
        this.digitizationService = digitizationService;
    }

    @GetMapping
    public List<DigitizationResponseDTO> listDigitizations() {
        return digitizationService.listDigitizations();
    }

    @GetMapping("/{id}")
    public DigitizationResponseDTO getDigitization(@PathVariable Long id) {
        return digitizationService.getDigitization(id);
    }

    @PostMapping
    public ResponseEntity<DigitizationResponseDTO> createDigitization(
            @Valid @RequestBody DigitizationCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(digitizationService.createDigitization(request));
    }
}