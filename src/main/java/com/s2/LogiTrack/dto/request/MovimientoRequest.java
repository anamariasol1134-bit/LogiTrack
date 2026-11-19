package com.s2.LogiTrack.dto.request;

import com.s2.LogiTrack.model.enums.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MovimientoRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipo,

        Long bodegaOrigenId,
        Long bodegaDestinoId,
        Long proveedorId,
        String observacion,

        @NotEmpty(message = "El movimiento debe tener al menos un producto")
        @Valid
        List<MovimientoItemRequest> items
) {}
