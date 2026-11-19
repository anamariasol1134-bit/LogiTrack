package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.vista.VInventarioTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VInventarioTotalRepository extends JpaRepository<VInventarioTotal, Long> {
    List<VInventarioTotal> findByBajoStockTrue();
}