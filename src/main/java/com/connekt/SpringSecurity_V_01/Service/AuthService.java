package com.connekt.SpringSecurity_V_01.Service;

import com.connekt.SpringSecurity_V_01.Entity.Role.Role;
import com.connekt.SpringSecurity_V_01.Entity.User;
import com.connekt.SpringSecurity_V_01.Error.PasswordsNotMatchException;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationRequest;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationResponse;
import com.connekt.SpringSecurity_V_01.Model.RegisterRequest;
import com.connekt.SpringSecurity_V_01.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepo userRepo;

    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request) throws PasswordsNotMatchException {

        if ( request.getPassword().equals(request.getConformPassword())) {

            User user = new User();

            BeanUtils.copyProperties(request, user);

            user.setRole(Role.USER);

            user.setPassword(passwordEncoder.encode(request.getPassword()));

            userRepo.save(user);

            String accessToken = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .build();

        }

        throw new PasswordsNotMatchException("\n\n\nPasswords Not Matched\n\n\n");

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User user = userRepo.findByEmail(request.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("\n\n\nUser Not Found\n\n\n")
        );

        String accessToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .build();

    }

}
