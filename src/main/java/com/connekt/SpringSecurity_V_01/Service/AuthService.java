package com.connekt.SpringSecurity_V_01.Service;

import com.connekt.SpringSecurity_V_01.Entity.Role.Role;
import com.connekt.SpringSecurity_V_01.Entity.Role.TokenType;
import com.connekt.SpringSecurity_V_01.Entity.Token;
import com.connekt.SpringSecurity_V_01.Entity.User;
import com.connekt.SpringSecurity_V_01.Error.PasswordsNotMatchException;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationRequest;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationResponse;
import com.connekt.SpringSecurity_V_01.Model.RegisterRequest;
import com.connekt.SpringSecurity_V_01.Repo.TokenRepo;
import com.connekt.SpringSecurity_V_01.Repo.UserRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepo userRepo;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final TokenRepo tokenRepo;

    private void saveToken(String accessToken, User user){

        Token token = Token.builder()
                .token(accessToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();

        tokenRepo.save(token);

    }

    private void revokeUserTokens(User user){

        List<Token> validUserTokens =tokenRepo.findAllValidTokensByUser(user.getId());

        if ( validUserTokens.isEmpty() ){

            return;

        }

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });

        tokenRepo.saveAll(validUserTokens);

    }

    public String register(RegisterRequest request) throws PasswordsNotMatchException {

        if ( request.getPassword().equals(request.getConformPassword())) {

            User user = new User();

            BeanUtils.copyProperties(request, user);

            user.setRole(Role.USER);

            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userRepo.save(user);

            return "User Successfully Registered";

        }

        throw new PasswordsNotMatchException("\n\n\nPasswords Not Matched\n\n\n");

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("\n\n\nUser Not Found\n\n\n")
        );

        String accessToken = jwtService.generateToken(user);

        String refreshToken = jwtService.generateRefreshToken(user);

        revokeUserTokens(user);

        saveToken(accessToken, user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if ( authHeader == null || !authHeader.startsWith("Bearer ") ) {

            return;

        }

        final String refreshToken = authHeader.substring(7);

        final String userEmail = jwtService.extractUserEmail(refreshToken);

        if ( userEmail != null ) {

            User userDetails = userRepo.findByEmail(userEmail).orElseThrow();

            if ( jwtService.isTokenValid(refreshToken, userDetails) ) {

                String accessToken = jwtService.generateToken(userDetails);

                revokeUserTokens(userDetails);

                saveToken(accessToken, userDetails);

                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

            }

        }

    }

}
