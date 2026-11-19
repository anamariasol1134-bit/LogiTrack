package com.s2.LogiTrack.controller;

import com.s2.LogiTrack.dto.request.LoginRequest;
import com.s2.LogiTrack.dto.request.RegisterRequest;
import com.s2.LogiTrack.dto.response.AuthResponse;
import com.s2.LogiTrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints públicos para iniciar sesión y registrarse")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y devuelve un Token JWT válido por 30 minutos.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una solicitud de cuenta en estado PENDIENTE. Un administrador debe aprobarla.")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registro exitoso. Espere la aprobación de un administrador.");
    }
}
