package com.demo.alzheimerassist.service.impl;

import org.springframework.stereotype.Service;

import com.demo.alzheimerassist.dto.RegisterRequest;
import com.demo.alzheimerassist.entity.User;
import com.demo.alzheimerassist.repository.UserRepository;
import com.demo.alzheimerassist.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public String register(RegisterRequest request) {

        if(repository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists";
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // We'll hash passwords later
        user.setAge(request.getAge());

        repository.save(user);

        return "Registration Successful";
    }
}
