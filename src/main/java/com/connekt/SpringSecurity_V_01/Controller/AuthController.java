package com.connekt.SpringSecurity_V_01.Controller;

import com.connekt.SpringSecurity_V_01.Error.PasswordsNotMatchException;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationRequest;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationResponse;
import com.connekt.SpringSecurity_V_01.Model.RegisterRequest;
import com.connekt.SpringSecurity_V_01.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) throws PasswordsNotMatchException {

        String authResponse = authService.register(request);

        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){

        AuthenticationResponse authResponse = authService.authenticate(request);

        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        authService.refreshToken(request, response);

    }

}
