package com.example.Pharmacy.service.impl;


import com.example.Pharmacy.dtos.request.UserRequest;
import com.example.Pharmacy.exception.UserException;
import com.example.Pharmacy.mapper.UserMapper;
import com.example.Pharmacy.model.Token;
import com.example.Pharmacy.model.User;
import com.example.Pharmacy.repo.TokenRepository;
import com.example.Pharmacy.repo.UserRepository;
import com.example.Pharmacy.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;

    @Override
    public String register(UserRequest request) {

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            user = userRepository.save(user);
            String jwt_token = jwtService.generateToken(user);
            saveUserToken(jwt_token, user);
            return jwt_token;
        } catch (Exception exception) {
            throw new UserException(exception.getMessage());
        }
    }

    @Override
    public String authenticate(UserRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            authentication = null;
        }
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user != null && authentication != null) {
            String jwt_token = jwtService.generateToken(user);
            revokeAllTokensByUser(user);
            saveUserToken(jwt_token, user);
            return jwt_token;
        } else {
            return null;
        }
    }

    private void revokeAllTokensByUser(User user) {
        List<Token> validTokensListByUser = tokenRepository.findAllTokens(user.getUsername());
        if (!validTokensListByUser.isEmpty()) {
            validTokensListByUser.forEach(t -> {
                t.setLoggedOut(true);
            });
        }
        tokenRepository.deleteAll(validTokensListByUser);
    }

    private void saveUserToken(String jwt_token, User user) {
        Token token = new Token();
        token.setToken(jwt_token);
        token.setUser(user);
        token.setLoggedOut(false);
        tokenRepository.save(token);
    }

}
