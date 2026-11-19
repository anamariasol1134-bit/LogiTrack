package com.s2.LogiTrack.model.vista;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;

@Entity
@Immutable
@Table(name = "v_ocupacion_bodega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VOcupacionBodega {

    @Id
    @Column(name = "bodega_id")
    private Long bodegaId;

    private String bodega;
    private Integer capacidad;

    @Column(name = "unidades_almacenadas")
    private Long unidadesAlmacenadas;

    @Column(name = "espacio_disponible")
    private Long espacioDisponible;

    @Column(name = "porcentaje_ocupacion")
    private BigDecimal porcentajeOcupacion;
}