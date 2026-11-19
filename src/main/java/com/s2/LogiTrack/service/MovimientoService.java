package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.request.MovimientoRequest;
import com.s2.LogiTrack.dto.response.MovimientoResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {
    MovimientoResponse registrar(MovimientoRequest request, String username);
    List<MovimientoResponse> listar();
    MovimientoResponse buscarPorId(Long id);
    List<MovimientoResponse> buscarEntreFechas(LocalDateTime desde, LocalDateTime hasta);
    void anular(Long id, String motivo);
}
