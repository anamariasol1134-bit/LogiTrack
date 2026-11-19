package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.Existencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExistenciaRepository extends JpaRepository<Existencia, Long> {
    Optional<Existencia> findByBodegaIdAndProductoId(Long bodegaId, Long productoId);
}
