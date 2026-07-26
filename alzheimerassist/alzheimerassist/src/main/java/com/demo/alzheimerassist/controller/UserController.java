package com.demo.alzheimerassist.controller;

import org.springframework.web.bind.annotation.*;

import com.demo.alzheimerassist.dto.RegisterRequest;
import com.demo.alzheimerassist.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        return service.register(request);

    }
}
