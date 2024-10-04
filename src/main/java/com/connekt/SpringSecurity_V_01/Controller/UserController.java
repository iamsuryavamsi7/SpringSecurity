package com.connekt.SpringSecurity_V_01.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/secured-end-point-user")
public class UserController {

    @GetMapping("/greet")
    public String greet(){

        return "Hello from secured-end-point USER";

    }

}
