package com.s2.LogiTrack.dto.response;

public record BodegaResponse(
        Long id,
        String nombre,
        String ubicacion,
        String municipio,
        Integer capacidad,
        String encargado
) {}
