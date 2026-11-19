package com.s2.LogiTrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El username es obligatorio")
        String username,
        @NotBlank(message = "El password es obligatorio")
        @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
        String password
) {}