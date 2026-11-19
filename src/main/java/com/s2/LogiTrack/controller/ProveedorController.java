package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.dto.request.ProveedorRequest;
import com.s2.LogiTrack.dto.response.ProveedorResponse;
import com.s2.LogiTrack.service.ProveedorService;
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
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Endpoints para la gestión de proveedores de productos")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Listar proveedores", description = "Obtiene una lista de todos los proveedores registrados.")
    public ResponseEntity<List<ProveedorResponse>> listar() {
        return ResponseEntity.ok(proveedorService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @Operation(summary = "Buscar proveedor por ID", description = "Retorna los detalles de un proveedor específico.")
    public ResponseEntity<ProveedorResponse> buscarPorId(
            @Parameter(description = "ID del proveedor a consultar") @PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear un proveedor", description = "Registra un nuevo proveedor. Solo ADMIN.")
    public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un proveedor", description = "Actualiza los datos de un proveedor. Solo ADMIN.")
    public ResponseEntity<ProveedorResponse> actualizar(
            @Parameter(description = "ID del proveedor a actualizar") @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar un proveedor", description = "Realiza un borrado lógico (activo=false) del proveedor. Solo ADMIN.")
    public ResponseEntity<Void> desactivar(
            @Parameter(description = "ID del proveedor a desactivar") @PathVariable Long id) {
        proveedorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}