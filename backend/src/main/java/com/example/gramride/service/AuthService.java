package com.example.gramride.service;

import com.example.gramride.dto.LoginRequestDTO;
import com.example.gramride.dto.SignupRequestDTO;
import com.example.gramride.entity.User;
import com.example.gramride.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User signup(SignupRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());


        return userRepository.save(user);
    }

    public User login(LoginRequestDTO request) {
        Optional<User> user = userRepository.findByEmailAndPasswordAndRole(
                request.getEmail(), request.getPassword(), request.getRole());

        return user.orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }
}
