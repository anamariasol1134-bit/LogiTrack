package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.auth.JwtUtil;
import com.s2.LogiTrack.dto.request.LoginRequest;
import com.s2.LogiTrack.dto.request.RegisterRequest;
import com.s2.LogiTrack.dto.response.AuthResponse;
import com.s2.LogiTrack.exception.ReglaNegocioException;
import com.s2.LogiTrack.model.Usuario;
import com.s2.LogiTrack.model.enums.EstadoCuenta;
import com.s2.LogiTrack.model.enums.Rol;
import com.s2.LogiTrack.repository.UsuarioRepository;
import com.s2.LogiTrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ReglaNegocioException("Usuario o contraseña incorrectos"));

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw new ReglaNegocioException("La cuenta no está activa. Contacte al administrador.");
        }

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new ReglaNegocioException("Usuario o contraseña incorrectos");
        }

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol().name());
        return new AuthResponse(token, usuario.getUsername(), usuario.getRol().name());
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new ReglaNegocioException("El username ya está en uso.");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .rol(Rol.EMPLEADO) // Registro público siempre es EMPLEADO
                .estadoCuenta(EstadoCuenta.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);
    }
}
