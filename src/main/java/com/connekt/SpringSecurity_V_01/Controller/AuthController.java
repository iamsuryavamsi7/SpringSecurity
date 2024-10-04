package com.connekt.SpringSecurity_V_01.Controller;

import com.connekt.SpringSecurity_V_01.Error.PasswordsNotMatchException;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationRequest;
import com.connekt.SpringSecurity_V_01.Model.AuthenticationResponse;
import com.connekt.SpringSecurity_V_01.Model.RegisterRequest;
import com.connekt.SpringSecurity_V_01.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) throws PasswordsNotMatchException {

        AuthenticationResponse authResponse = authService.register(request);

        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){

        AuthenticationResponse authResponse = authService.authenticate(request);

        return ResponseEntity.ok(authResponse);

    }

}
