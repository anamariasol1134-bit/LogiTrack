package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.response.UsuarioResponse;
import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listarPendientes();
    void aprobarUsuario(Long id, String adminUsername);
    void rechazarUsuario(Long id, String motivo, String adminUsername);
}
