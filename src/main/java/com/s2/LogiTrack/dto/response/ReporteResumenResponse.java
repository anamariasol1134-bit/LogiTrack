package com.s2.LogiTrack.dto.response;

import com.s2.LogiTrack.model.vista.VOcupacionBodega;
import com.s2.LogiTrack.model.vista.VRankingMovimientos;

import java.util.List;

public record ReporteResumenResponse(
        List<VOcupacionBodega> stockPorBodega,
        List<VRankingMovimientos> productosMasMovidos,
        Long cantidadProductosStockBajo
) {}