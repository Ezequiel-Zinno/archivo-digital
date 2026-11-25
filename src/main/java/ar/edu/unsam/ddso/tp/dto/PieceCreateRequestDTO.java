package ar.edu.unsam.ddso.tp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PieceCreateRequestDTO(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 2000) String description,
        @NotNull LocalDate date) {}