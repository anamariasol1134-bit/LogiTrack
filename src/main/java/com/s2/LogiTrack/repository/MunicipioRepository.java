package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
    boolean existsByNombreAndDepartamento(String nombre, String departamento);
}