package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.dto.request.MovimientoRequest;
import com.s2.LogiTrack.dto.response.MovimientoResponse;
import com.s2.LogiTrack.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Endpoints para registrar y consultar Entradas, Salidas y Transferencias de stock")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Registrar un movimiento", description = "Registra una ENTRADA, SALIDA o TRANSFERENCIA y actualiza automáticamente las existencias de las bodegas.")
    public ResponseEntity<MovimientoResponse> registrar(@Valid @RequestBody MovimientoRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrar(request, username));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Listar movimientos", description = "Obtiene el historial completo de movimientos registrados.")
    public ResponseEntity<List<MovimientoResponse>> listar() {
        return ResponseEntity.ok(movimientoService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Buscar movimiento por ID", description = "Retorna los detalles de un movimiento específico.")
    public ResponseEntity<MovimientoResponse> buscarPorId(
            @Parameter(description = "ID del movimiento a consultar") @PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.buscarPorId(id));
    }

    @GetMapping("/entre-fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Buscar movimientos entre fechas", description = "Filtra los movimientos registrados en un rango de fechas específico.")
    public ResponseEntity<List<MovimientoResponse>> buscarEntreFechas(
            @Parameter(description = "Fecha de inicio (formato ISO: 2026-11-16T00:00:00)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @Parameter(description = "Fecha de fin (formato ISO: 2026-11-16T23:59:59)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return ResponseEntity.ok(movimientoService.buscarEntreFechas(desde, hasta));
    }

    @PutMapping("/{id}/anular")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Anular un movimiento", description = "Aplica el movimiento inverso sobre las existencias, cambia el estado a ANULADO y registra el motivo. Solo ADMIN.")
    public ResponseEntity<Void> anular(
            @Parameter(description = "ID del movimiento a anular") @PathVariable Long id,
            @Parameter(description = "Motivo por el cual se anula el movimiento") @RequestParam String motivo) {
        movimientoService.anular(id, motivo);
        return ResponseEntity.noContent().build();
    }
}