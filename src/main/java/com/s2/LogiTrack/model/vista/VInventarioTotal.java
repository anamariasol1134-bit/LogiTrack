package com.s2.LogiTrack.model.vista;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "v_inventario_total")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VInventarioTotal {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    private String sku;
    private String nombre;
    private String categoria;
    private BigDecimal precio;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    private Boolean activo;

    // --- NUEVO CAMPO PARA EL PROVEEDOR ---
    @Column(name = "proveedor_id")
    private Long proveedorId;

    @Column(name = "stock_total")
    private Long stockTotal;

    @Column(name = "bajo_stock")
    private Boolean bajoStock;
}