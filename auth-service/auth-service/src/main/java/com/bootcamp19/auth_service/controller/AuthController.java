package com.bootcamp19.auth_service.controller;

import com.bootcamp19.auth_service.dto.request.LoginRequestDto;
import com.bootcamp19.auth_service.dto.request.RegisterRequestDto;
import com.bootcamp19.auth_service.dto.response.AuthResponseDto;
import com.bootcamp19.auth_service.dto.response.BaseResponse;
import com.bootcamp19.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<String> register(@Valid @RequestBody RegisterRequestDto request) {
        return BaseResponse.ok("User registered successfully", authService.register(request));
    }

    @PostMapping("/login")
    public BaseResponse<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return BaseResponse.ok("Login successful", authService.login(request));
    }
}
