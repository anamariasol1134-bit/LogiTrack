package com.s2.LogiTrack.model.listener;

import com.s2.LogiTrack.config.ApplicationContextProvider;
import com.s2.LogiTrack.model.BitacoraCambios;
import com.s2.LogiTrack.model.enums.OperacionBitacora;
import com.s2.LogiTrack.repository.BitacoraCambiosRepository;
import jakarta.persistence.PostPersist; // <-- Cambiado a PostPersist para tener el ID
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AuditoriaListener {

    // Usamos PostPersist para que el ID ya esté asignado
    @PostPersist
    public void prePersist(Object entidad) {
        registrarAuditoria(OperacionBitacora.INSERTAR, entidad, null);
    }

    @PreUpdate
    public void preUpdate(Object entidad) {
        registrarAuditoria(OperacionBitacora.ACTUALIZAR, entidad, null);
    }

    @PreRemove
    public void preRemove(Object entidad) {
        registrarAuditoria(OperacionBitacora.BORRAR, entidad, null);
    }

    private void registrarAuditoria(OperacionBitacora operacion, Object entidad, Map<String, Object> valoresAnteriores) {
        BitacoraCambiosRepository repository = ApplicationContextProvider.getBean(BitacoraCambiosRepository.class);

        BitacoraCambios bitacora = new BitacoraCambios();
        bitacora.setOperacion(operacion);
        bitacora.setFechaHora(LocalDateTime.now());
        bitacora.setEntidad(entidad.getClass().getSimpleName());

        // Obtener el ID (Ahora sí funcionará gracias a @PostPersist)
        try {
            Object id = entidad.getClass().getMethod("getId").invoke(entidad);
            if (id != null) {
                bitacora.setEntidadId(Long.valueOf(id.toString()));
            }
        } catch (Exception e) {
            // Ignorar
        }

        // Obtener usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated()) ? auth.getName() : "SISTEMA";
        bitacora.setUsuario(usuario);

        // Mapear valores nuevos
        Map<String, Object> valoresNuevos = new HashMap<>();

        // Si es Producto
        if (entidad instanceof com.s2.LogiTrack.model.Producto) {
            com.s2.LogiTrack.model.Producto p = (com.s2.LogiTrack.model.Producto) entidad;
            valoresNuevos.put("sku", p.getSku());
            valoresNuevos.put("nombre", p.getNombre());
            valoresNuevos.put("precio", p.getPrecio());
        }
        // Si es Bodega
        else if (entidad instanceof com.s2.LogiTrack.model.Bodega) {
            com.s2.LogiTrack.model.Bodega b = (com.s2.LogiTrack.model.Bodega) entidad;
            valoresNuevos.put("nombre", b.getNombre());
            valoresNuevos.put("capacidad", b.getCapacidad());
        }
        // Si es Movimiento
        else if (entidad instanceof com.s2.LogiTrack.model.Movimiento) {
            com.s2.LogiTrack.model.Movimiento m = (com.s2.LogiTrack.model.Movimiento) entidad;
            valoresNuevos.put("tipo", m.getTipo().name());
            valoresNuevos.put("estado", m.getEstado().name());
            if (m.getObservacion() != null) valoresNuevos.put("observacion", m.getObservacion());
        }
        // Si es Usuario
        else if (entidad instanceof com.s2.LogiTrack.model.Usuario) {
            com.s2.LogiTrack.model.Usuario u = (com.s2.LogiTrack.model.Usuario) entidad;
            valoresNuevos.put("username", u.getUsername());
            valoresNuevos.put("estado", u.getEstadoCuenta().name());
        }
        // Si es Proveedor
        else if (entidad instanceof com.s2.LogiTrack.model.Proveedor) {
            com.s2.LogiTrack.model.Proveedor prov = (com.s2.LogiTrack.model.Proveedor) entidad;
            valoresNuevos.put("nombre", prov.getNombre());
            valoresNuevos.put("nit", prov.getNit());
        }

        bitacora.setValoresNuevos(valoresNuevos);
        repository.save(bitacora);
    }
}