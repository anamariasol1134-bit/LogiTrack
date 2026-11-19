package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.request.ProductoRequest;
import com.s2.LogiTrack.dto.response.ProductoResponse;
import java.util.List;

public interface ProductoService {
    ProductoResponse crear(ProductoRequest request);
    List<ProductoResponse> listar();
    ProductoResponse buscarPorId(Long id);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void desactivar(Long id);
    List<ProductoResponse> listarStockBajo();
}