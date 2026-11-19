package com.s2.LogiTrack.dto.response;

public record AuthResponse(
        String token,
        String username,
        String rol
) {}