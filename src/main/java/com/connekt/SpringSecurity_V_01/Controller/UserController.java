package com.connekt.SpringSecurity_V_01.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/secured-end-point-user")
public class UserController {

    @GetMapping("/greet")
    public ResponseEntity<String> greet(){

        String greetMessage =  "Hello from secured-end-point USER";

        return ResponseEntity.ok(greetMessage);

    }

    @GetMapping("/get-username")
    public String getUsername(){

        return "";

    }

}
