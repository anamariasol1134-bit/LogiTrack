package com.s2.LogiTrack.dto.response;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String sku,
        String nombre,
        String categoria,
        BigDecimal precio,
        Integer stockMinimo,
        Long stockTotal,
        Boolean activo,
        String proveedorNombre
) {}