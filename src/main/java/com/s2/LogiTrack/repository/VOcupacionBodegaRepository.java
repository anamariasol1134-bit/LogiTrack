package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.vista.VOcupacionBodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VOcupacionBodegaRepository extends JpaRepository<VOcupacionBodega, Long> {
}
