package com.connekt.SpringSecurity_V_01.Controller;

import com.connekt.SpringSecurity_V_01.Model.GetUserNameRequest;
import com.connekt.SpringSecurity_V_01.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secured-end-point-user")
public class UserController {

    private final JwtService jwtService;

    @GetMapping("/greet")
    public ResponseEntity<String> greet(){

        String greetMessage =  "Hello from secured-end-point USER";

        return ResponseEntity.ok(greetMessage);

    }

    @PostMapping("/get-username")
    public ResponseEntity<String> getUsername(@RequestBody GetUserNameRequest request){

        String userName = jwtService.extractUserEmail(request.getJwtToken());

        return ResponseEntity.ok(userName);

    }

}
