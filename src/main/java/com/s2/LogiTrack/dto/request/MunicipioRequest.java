package com.s2.LogiTrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MunicipioRequest(
        @NotBlank(message = "El nombre del municipio es obligatorio")
        String nombre,

        @NotBlank(message = "El departamento es obligatorio")
        String departamento
) {}