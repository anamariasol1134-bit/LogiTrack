package com.s2.LogiTrack.dto.response;

public record ProveedorResponse(
        Long id,
        String nombre,
        String nit,
        String telefono,
        String correo,
        String municipio,
        Boolean activo
) {}