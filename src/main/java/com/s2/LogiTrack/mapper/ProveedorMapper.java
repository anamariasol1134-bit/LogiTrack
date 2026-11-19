package com.s2.LogiTrack.mapper;

import com.s2.LogiTrack.dto.request.ProveedorRequest;
import com.s2.LogiTrack.dto.response.ProveedorResponse;
import com.s2.LogiTrack.model.Municipio;
import com.s2.LogiTrack.model.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {
    public Proveedor toEntity(ProveedorRequest request, Municipio municipio) {
        return Proveedor.builder()
                .nombre(request.nombre())
                .nit(request.nit())
                .telefono(request.telefono())
                .correo(request.correo())
                .municipio(municipio)
                .activo(true)
                .build();
    }

    public ProveedorResponse toResponse(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getId(),
                proveedor.getNombre(),
                proveedor.getNit(),
                proveedor.getTelefono(),
                proveedor.getCorreo(),
                proveedor.getMunicipio().getNombre(),
                proveedor.getActivo()
        );
    }
}
