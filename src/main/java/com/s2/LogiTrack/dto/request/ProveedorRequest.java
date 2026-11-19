package com.s2.LogiTrack.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProveedorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El NIT es obligatorio")
        String nit,

        String telefono,

        @Email(message = "El correo debe tener un formato válido")
        String correo,

        @NotNull(message = "El municipio es obligatorio")
        Long municipioId
) {}