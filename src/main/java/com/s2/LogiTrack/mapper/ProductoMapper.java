package com.s2.LogiTrack.mapper;

import com.s2.LogiTrack.dto.request.ProductoRequest;
import com.s2.LogiTrack.dto.response.ProductoResponse;
import com.s2.LogiTrack.model.Producto;
import com.s2.LogiTrack.model.Proveedor;
import com.s2.LogiTrack.model.vista.VInventarioTotal;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    // Ahora recibe el Proveedor
    public Producto toEntity(ProductoRequest request, Proveedor proveedor) {
        return Producto.builder()
                .sku(request.sku())
                .nombre(request.nombre())
                .categoria(request.categoria())
                .precio(request.precio())
                .stockMinimo(request.stockMinimo())
                .proveedor(proveedor) // Asignamos el proveedor
                .activo(true)
                .build();
    }

    public ProductoResponse toResponse(Producto producto, Long stockTotal) {
        return new ProductoResponse(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getStockMinimo(),
                stockTotal,
                producto.getActivo(),
                producto.getProveedor() != null ? producto.getProveedor().getNombre() : null
        );
    }

    // Ahora recibe el nombre del proveedor (que sacaremos de la vista)
    public ProductoResponse toResponseFromVista(VInventarioTotal vista, String proveedorNombre) {
        return new ProductoResponse(
                vista.getProductoId(),
                vista.getSku(),
                vista.getNombre(),
                vista.getCategoria(),
                vista.getPrecio(),
                vista.getStockMinimo(),
                vista.getStockTotal(),
                vista.getActivo(),
                proveedorNombre
        );
    }
}