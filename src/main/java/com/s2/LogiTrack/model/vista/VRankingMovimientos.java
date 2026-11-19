package com.s2.LogiTrack.model.vista;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "v_ranking_movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VRankingMovimientos {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    private String nombre;

    @Column(name = "total_movimientos")
    private Long totalMovimientos;

    @Column(name = "unidades_movidas")
    private Long unidadesMovidas;
}
