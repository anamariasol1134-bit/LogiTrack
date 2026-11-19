package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.dto.request.BodegaRequest;
import com.s2.LogiTrack.dto.response.BodegaResponse;
import com.s2.LogiTrack.service.BodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas", description = "Endpoints para la gestión de las bodegas de LogiTrack")
public class BodegaController {

    private final BodegaService bodegaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Listar todas las bodegas", description = "Obtiene una lista con todas las bodegas registradas en el sistema.")
    public ResponseEntity<List<BodegaResponse>> listar() {
        return ResponseEntity.ok(bodegaService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Buscar bodega por ID", description = "Retorna los detalles de una bodega específica.")
    public ResponseEntity<BodegaResponse> buscarPorId(
            @Parameter(description = "ID único de la bodega a consultar") @PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una bodega", description = "Registra una nueva bodega. Solo disponible para usuarios con rol ADMIN.")
    public ResponseEntity<BodegaResponse> crear(@Valid @RequestBody BodegaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar una bodega", description = "Actualiza los datos de una bodega existente. Solo para ADMIN.")
    public ResponseEntity<BodegaResponse> actualizar(
            @Parameter(description = "ID de la bodega a actualizar") @PathVariable Long id,
            @Valid @RequestBody BodegaRequest request) {
        return ResponseEntity.ok(bodegaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar una bodega", description = "Borra la bodega siempre y cuando no tenga productos en existencia. Solo ADMIN.")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la bodega a eliminar") @PathVariable Long id) {
        bodegaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
