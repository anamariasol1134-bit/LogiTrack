package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.vista.VRankingMovimientos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VRankingMovimientosRepository extends JpaRepository<VRankingMovimientos, Long> {
    List<VRankingMovimientos> findTop5ByOrderByUnidadesMovidasDesc();
}