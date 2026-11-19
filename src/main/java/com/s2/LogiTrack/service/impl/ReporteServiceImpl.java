package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.response.ReporteResumenResponse;
import com.s2.LogiTrack.repository.VInventarioTotalRepository;
import com.s2.LogiTrack.repository.VOcupacionBodegaRepository;
import com.s2.LogiTrack.repository.VRankingMovimientosRepository;
import com.s2.LogiTrack.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final VOcupacionBodegaRepository ocupacionRepo;
    private final VRankingMovimientosRepository rankingRepo;
    private final VInventarioTotalRepository inventarioRepo;

    @Override
    @Transactional(readOnly = true)
    public ReporteResumenResponse obtenerResumen() {
        var stockPorBodega = ocupacionRepo.findAll();
        var top5Movidos = rankingRepo.findTop5ByOrderByUnidadesMovidasDesc();
        Long stockBajo = (long) inventarioRepo.findByBajoStockTrue().size();

        return new ReporteResumenResponse(stockPorBodega, top5Movidos, stockBajo);
    }
}