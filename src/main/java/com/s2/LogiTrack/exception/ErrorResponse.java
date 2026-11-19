package com.s2.LogiTrack.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta
) {
    public ErrorResponse(int status, String error, String mensaje, String ruta) {
        this(LocalDateTime.now(), status, error, mensaje, ruta);
    }
}
