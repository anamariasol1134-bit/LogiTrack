package com.s2.LogiTrack.repository;

import com.s2.LogiTrack.model.BitacoraCambios;
import com.s2.LogiTrack.model.enums.OperacionBitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BitacoraCambiosRepository extends JpaRepository<BitacoraCambios, Long> {
    List<BitacoraCambios> findByUsuario(String usuario);
    List<BitacoraCambios> findByOperacion(OperacionBitacora operacion);
    List<BitacoraCambios> findByUsuarioAndOperacion(String usuario, OperacionBitacora operacion);
}
