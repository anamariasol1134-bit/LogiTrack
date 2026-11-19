package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.vista.VInventarioPorBodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VInventarioPorBodegaRepository extends JpaRepository<VInventarioPorBodega, Long> {
    List<VInventarioPorBodega> findByBodegaId(Long bodegaId);
}