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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthenticationResponse register(RegisterRequest request) throws PasswordsNotMatchException {

        if ( request.getPassword().equals(request.getConformPassword())) {

            User user = new User();

            BeanUtils.copyProperties(request, user);

            user.setRole(Role.USER);

            user.setPassword(passwordEncoder.encode(request.getPassword()));

            User savedUser = userRepo.save(user);

            String accessToken = jwtService.generateToken(savedUser);

            saveToken(accessToken, savedUser);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .build();

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

        revokeUserTokens(user);

        saveToken(accessToken, user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();

    }

}
