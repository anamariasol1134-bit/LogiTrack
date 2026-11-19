package com.s2.LogiTrack.model.vista;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "v_inventario_por_bodega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VInventarioPorBodega {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "bodega_id")
    private Long bodegaId;

    private String bodega;
    private String producto;
    private Integer cantidad;
}
