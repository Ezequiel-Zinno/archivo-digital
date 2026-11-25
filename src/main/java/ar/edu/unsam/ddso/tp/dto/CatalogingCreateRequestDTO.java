package ar.edu.unsam.ddso.tp.dto;

import jakarta.validation.constraints.NotNull;

public record CatalogingCreateRequestDTO(@NotNull Long tagId) {}
