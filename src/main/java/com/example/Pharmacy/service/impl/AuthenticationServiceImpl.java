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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.Pharmacy.messages.User.UserExceptionMessages.INVALID_USERNAME_PASSWORD;
import static com.example.Pharmacy.messages.User.UserExceptionMessages.USER_NOT_FOUND;
import static com.example.Pharmacy.messages.User.UserLogMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;

    /**
     * Register a new user
     *
     * @param request
     * @return
     */
    @Override
    public String register(UserRequest request) {

        // Convert request to user
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            user = userRepository.save(user);
            String jwtToken = jwtService.generateToken(user);
            saveUserToken(jwtToken, user);
            log.info(LOG_USER_REGISTERED_SUCCESSFULLY);
            return jwtToken;
        } catch (Exception exception) {
            log.error(String.format(LOG_UNABLE_TO_REGISTER_USER, exception.getMessage()));
            throw new UserException(exception.getMessage());
        }
    }

    /**
     * Authenticate a user
     *
     * @param request
     * @return
     */
    @Override
    public String authenticate(UserRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (Exception e) {
            authentication = null;
        }
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> {
            log.error(LOG_USER_NOT_FOUND);
            return new UserException(USER_NOT_FOUND);
        });
        if (user != null && authentication != null) {
            String jwtToken = jwtService.generateToken(user);
            revokeAllTokensByUser(user);
            saveUserToken(jwtToken, user);
            return jwtToken;
        } else {
            throw new UserException(INVALID_USERNAME_PASSWORD);
        }
    }

    /**
     * Revoke all tokens by user
     *
     * @param user
     */
    private void revokeAllTokensByUser(User user) {
        // Get all valid tokens by user
        List<Token> validTokensListByUser = tokenRepository.findAllTokens(user.getUsername());
        if (!validTokensListByUser.isEmpty()) {
            validTokensListByUser.forEach(t -> {
                t.setLoggedOut(true);
            });
        }
        // Delete all valid tokens by user
        tokenRepository.deleteAll(validTokensListByUser);
        log.info(LOG_TOKEN_DELETED_SUCCESSFULLY);
    }

    /**
     * Save user token
     *
     * @param jwtToken
     * @param user
     */
    private void saveUserToken(String jwtToken, User user) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setUser(user);
        token.setLoggedOut(false);
        tokenRepository.save(token);
        log.info(LOG_TOKEN_SAVED_SUCCESSFULLY);
    }

}
