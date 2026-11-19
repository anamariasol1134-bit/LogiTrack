package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.request.ProveedorRequest;
import com.s2.LogiTrack.dto.response.ProveedorResponse;
import java.util.List;

public interface ProveedorService {
    ProveedorResponse crear(ProveedorRequest request);
    List<ProveedorResponse> listar();
    ProveedorResponse buscarPorId(Long id);
    ProveedorResponse actualizar(Long id, ProveedorRequest request);
    void desactivar(Long id);
}