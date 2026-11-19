package com.s2.LogiTrack.mapper;

import com.s2.LogiTrack.dto.response.MovimientoItemResponse;
import com.s2.LogiTrack.dto.response.MovimientoResponse;
import com.s2.LogiTrack.model.Movimiento;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MovimientoMapper {

    public MovimientoResponse toResponse(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipo().name(),
                movimiento.getEstado().name(),
                movimiento.getUsuario() != null ? movimiento.getUsuario().getUsername() : "N/A",
                movimiento.getBodegaOrigen() != null ? movimiento.getBodegaOrigen().getNombre() : null,
                movimiento.getBodegaDestino() != null ? movimiento.getBodegaDestino().getNombre() : null,
                movimiento.getProveedor() != null ? movimiento.getProveedor().getNombre() : null,
                movimiento.getObservacion(),
                movimiento.getItems().stream().map(item -> new MovimientoItemResponse(
                        item.getProducto().getId(),
                        item.getProducto().getNombre(),
                        item.getCantidad(),
                        item.getPrecioUnitario()
                )).collect(Collectors.toList())
        );
    }
}
