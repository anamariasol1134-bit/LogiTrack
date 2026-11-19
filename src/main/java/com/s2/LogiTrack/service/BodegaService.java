package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.request.BodegaRequest;
import com.s2.LogiTrack.dto.response.BodegaResponse;
import java.util.List;

public interface BodegaService {
    BodegaResponse crear(BodegaRequest request);
    List<BodegaResponse> listar();
    BodegaResponse buscarPorId(Long id);
    BodegaResponse actualizar(Long id, BodegaRequest request);
    void eliminar(Long id);
}