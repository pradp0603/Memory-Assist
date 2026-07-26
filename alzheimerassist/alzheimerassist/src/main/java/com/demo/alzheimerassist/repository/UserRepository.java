package com.demo.alzheimerassist.repository;

import java.util.Optional;

import com.demo.alzheimerassist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
