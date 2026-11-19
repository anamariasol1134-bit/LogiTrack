package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.request.ProductoRequest;
import com.s2.LogiTrack.dto.response.ProductoResponse;
import com.s2.LogiTrack.exception.ReglaNegocioException;
import com.s2.LogiTrack.exception.ResourceNotFoundException;
import com.s2.LogiTrack.mapper.ProductoMapper;
import com.s2.LogiTrack.model.Bodega;
import com.s2.LogiTrack.model.Existencia;
import com.s2.LogiTrack.model.Producto;
import com.s2.LogiTrack.model.Proveedor;
import com.s2.LogiTrack.model.vista.VInventarioTotal;
import com.s2.LogiTrack.repository.*;
import com.s2.LogiTrack.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final VInventarioTotalRepository vistaRepository;
    private final ProductoMapper productoMapper;
    private final BodegaRepository bodegaRepository;
    private final ExistenciaRepository existenciaRepository;
    private final ProveedorRepository proveedorRepository; // NUEVO

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsBySku(request.sku())) {
            throw new ReglaNegocioException("Ya existe un producto con ese SKU.");
        }

        Proveedor proveedor = null;
        if (request.proveedorId() != null) {
            proveedor = proveedorRepository.findById(request.proveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        }

        Producto producto = productoMapper.toEntity(request, proveedor);
        Producto guardado = productoRepository.save(producto);

        List<Bodega> bodegas = bodegaRepository.findAll();
        for (Bodega bodega : bodegas) {
            Existencia existencia = new Existencia();
            existencia.setBodega(bodega);
            existencia.setProducto(guardado);
            existencia.setCantidad(0);
            existencia.setVersion(0);
            existenciaRepository.save(existencia);
        }

        return productoMapper.toResponse(guardado, 0L);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return vistaRepository.findAll().stream().map(vista -> {
            String provNombre = null;
            if (vista.getProveedorId() != null) {
                provNombre = proveedorRepository.findById(vista.getProveedorId())
                        .map(Proveedor::getNombre).orElse(null);
            }
            return productoMapper.toResponseFromVista(vista, provNombre);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        VInventarioTotal vista = vistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado en la vista de inventario"));

        String provNombre = producto.getProveedor() != null ? producto.getProveedor().getNombre() : null;
        return productoMapper.toResponseFromVista(vista, provNombre);
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Proveedor proveedor = null;
        if (request.proveedorId() != null) {
            proveedor = proveedorRepository.findById(request.proveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
        }

        producto.setSku(request.sku());
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setPrecio(request.precio());
        producto.setStockMinimo(request.stockMinimo());
        producto.setProveedor(proveedor);

        Producto actualizado = productoRepository.save(producto);

        VInventarioTotal vista = vistaRepository.findById(id).orElse(null);
        Long stockTotal = vista != null ? vista.getStockTotal() : 0L;

        return productoMapper.toResponse(actualizado, stockTotal);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarStockBajo() {
        return vistaRepository.findByBajoStockTrue().stream().map(vista -> {
            String provNombre = null;
            if (vista.getProveedorId() != null) {
                provNombre = proveedorRepository.findById(vista.getProveedorId())
                        .map(Proveedor::getNombre).orElse(null);
            }
            return productoMapper.toResponseFromVista(vista, provNombre);
        }).collect(Collectors.toList());
    }
}