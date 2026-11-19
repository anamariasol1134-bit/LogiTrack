package com.s2.LogiTrack.dto.response;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String username,
        String rol,
        String estadoCuenta,
        LocalDateTime fechaCreacion
) {}