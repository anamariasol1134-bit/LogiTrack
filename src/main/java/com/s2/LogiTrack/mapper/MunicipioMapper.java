package com.s2.LogiTrack.mapper;

import com.s2.LogiTrack.dto.request.MunicipioRequest;
import com.s2.LogiTrack.dto.response.MunicipioResponse;
import com.s2.LogiTrack.model.Municipio;
import org.springframework.stereotype.Component;

@Component
public class MunicipioMapper {
    public Municipio toEntity(MunicipioRequest request) {
        return Municipio.builder()
                .nombre(request.nombre())
                .departamento(request.departamento())
                .build();
    }

    public MunicipioResponse toResponse(Municipio municipio) {
        return new MunicipioResponse(
                municipio.getId(),
                municipio.getNombre(),
                municipio.getDepartamento()
        );
    }
}
