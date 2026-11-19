package com.s2.LogiTrack.dto.response;

import java.math.BigDecimal;

public record MovimientoItemResponse(
        Long productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioUnitario
) {}