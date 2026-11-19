package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.dto.response.UsuarioResponse;
import com.s2.LogiTrack.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de cuentas de usuario y solicitudes de registro")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar cuentas pendientes", description = "Muestra todos los usuarios que se han registrado y esperan aprobación del administrador.")
    public ResponseEntity<List<UsuarioResponse>> listarPendientes() {
        return ResponseEntity.ok(usuarioService.listarPendientes());
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aprobar cuenta", description = "Activa la cuenta de un usuario pendiente y registra quién la aprobó.")
    public ResponseEntity<Void> aprobar(
            @Parameter(description = "ID del usuario a aprobar") @PathVariable Long id) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.aprobarUsuario(id, adminUsername);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechazar cuenta", description = "Rechaza la solicitud de un usuario y guarda el motivo del rechazo.")
    public ResponseEntity<Void> rechazar(
            @Parameter(description = "ID del usuario a rechazar") @PathVariable Long id,
            @Parameter(description = "Motivo por el cual se rechaza la cuenta") @RequestParam String motivo) {
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.rechazarUsuario(id, motivo, adminUsername);
        return ResponseEntity.noContent().build();
    }
}