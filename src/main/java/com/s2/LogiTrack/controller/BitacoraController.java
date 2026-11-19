package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.model.BitacoraCambios;
import com.s2.LogiTrack.model.enums.OperacionBitacora;
import com.s2.LogiTrack.repository.BitacoraCambiosRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bitacora")
@RequiredArgsConstructor
@Tag(name = "Bitácora / Auditoría", description = "Consultas del historial de cambios del sistema (Solo ADMIN)")
public class BitacoraController {

    private final BitacoraCambiosRepository bitacoraRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar bitácora", description = "Obtiene el registro de auditoría. Permite filtrar por usuario y/o tipo de operación.")
    public ResponseEntity<List<BitacoraCambios>> consultar(
            @Parameter(description = "Nombre de usuario para filtrar (opcional)")
            @RequestParam(required = false) String usuario,
            @Parameter(description = "Tipo de operación: INSERTAR, ACTUALIZAR o BORRAR (opcional)")
            @RequestParam(required = false) OperacionBitacora operacion) {

        if (usuario != null && operacion != null) {
            return ResponseEntity.ok(bitacoraRepository.findByUsuarioAndOperacion(usuario, operacion));
        } else if (usuario != null) {
            return ResponseEntity.ok(bitacoraRepository.findByUsuario(usuario));
        } else if (operacion != null) {
            return ResponseEntity.ok(bitacoraRepository.findByOperacion(operacion));
        }
        return ResponseEntity.ok(bitacoraRepository.findAll());
    }
}