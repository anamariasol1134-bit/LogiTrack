package com.s2.LogiTrack.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MovimientoResponse(
        Long id,
        LocalDateTime fecha,
        String tipo,
        String estado,
        String usuario,
        String bodegaOrigen,
        String bodegaDestino,
        String proveedor,
        String observacion,
        List<MovimientoItemResponse> items
) {}
