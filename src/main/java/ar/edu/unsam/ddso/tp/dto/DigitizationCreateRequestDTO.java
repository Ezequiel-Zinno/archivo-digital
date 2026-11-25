package ar.edu.unsam.ddso.tp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DigitizationCreateRequestDTO(
        @NotBlank @Size(max = 2000) String url,
        @NotBlank @Size(max = 2000) String description,
        @NotNull Long pieceId) {}