package com.s2.LogiTrack.mapper;

import com.s2.LogiTrack.dto.request.BodegaRequest;
import com.s2.LogiTrack.dto.response.BodegaResponse;
import com.s2.LogiTrack.model.Bodega;
import com.s2.LogiTrack.model.Municipio;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public Bodega toEntity(BodegaRequest request, Municipio municipio) {
        return Bodega.builder()
                .nombre(request.nombre())
                .ubicacion(request.ubicacion())
                .municipio(municipio)
                .capacidad(request.capacidad())
                .encargado(request.encargado())
                .build();
    }

    public BodegaResponse toResponse(Bodega bodega) {
        return new BodegaResponse(
                bodega.getId(),
                bodega.getNombre(),
                bodega.getUbicacion(),
                bodega.getMunicipio().getNombre(), // Asumiendo que se consulta con JOIN
                bodega.getCapacidad(),
                bodega.getEncargado()
        );
    }
}