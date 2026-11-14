package com.bootcamp19.auth_service.service.impl;

import com.bootcamp19.auth_service.dto.request.LoginRequestDto;
import com.bootcamp19.auth_service.dto.request.RegisterRequestDto;
import com.bootcamp19.auth_service.dto.response.AuthResponseDto;
import com.bootcamp19.auth_service.entity.User;
import com.bootcamp19.auth_service.exception.UnauthorizedException;
import com.bootcamp19.auth_service.exception.UserAlreadyExistException;
import com.bootcamp19.auth_service.repository.UserRepository;
import com.bootcamp19.auth_service.service.AuthService;
import com.bootcamp19.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistException(request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "User " + user.getUsername() + " successfully registered";
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponseDto(user.getUsername(), token);
    }

}
