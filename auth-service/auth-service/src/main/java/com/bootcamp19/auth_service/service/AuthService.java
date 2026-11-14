package com.bootcamp19.auth_service.service;

import com.bootcamp19.auth_service.dto.request.LoginRequestDto;
import com.bootcamp19.auth_service.dto.request.RegisterRequestDto;
import com.bootcamp19.auth_service.dto.response.AuthResponseDto;

public interface AuthService {
    String register(RegisterRequestDto request);
    AuthResponseDto login(LoginRequestDto request);
}