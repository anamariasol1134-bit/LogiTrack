package com.s2.LogiTrack.service;

import com.s2.LogiTrack.dto.request.LoginRequest;
import com.s2.LogiTrack.dto.request.RegisterRequest;
import com.s2.LogiTrack.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    void register(RegisterRequest request);
}
