package com.s2.LogiTrack.service.impl;

import com.s2.LogiTrack.dto.response.UsuarioResponse;
import com.s2.LogiTrack.exception.ResourceNotFoundException;
import com.s2.LogiTrack.model.Usuario;
import com.s2.LogiTrack.model.enums.EstadoCuenta;
import com.s2.LogiTrack.repository.UsuarioRepository;
import com.s2.LogiTrack.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarPendientes() {
        return usuarioRepository.findByEstadoCuenta(EstadoCuenta.PENDIENTE)
                .stream()
                .map(u -> new UsuarioResponse(
                        u.getId(),
                        u.getNombre(),
                        u.getUsername(),
                        u.getRol().name(),
                        u.getEstadoCuenta().name(),
                        u.getFechaCreacion()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void aprobarUsuario(Long id, String adminUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Usuario admin = usuarioRepository.findByUsername(adminUsername).orElse(null);

        usuario.setEstadoCuenta(EstadoCuenta.ACTIVA);
        usuario.setAprobadoPor(admin);
        usuario.setFechaAprobacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void rechazarUsuario(Long id, String motivo, String adminUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Usuario admin = usuarioRepository.findByUsername(adminUsername).orElse(null);

        usuario.setEstadoCuenta(EstadoCuenta.RECHAZADA);
        usuario.setMotivoRechazo(motivo);
        usuario.setAprobadoPor(admin);
        usuario.setFechaAprobacion(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
}