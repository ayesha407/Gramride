package com.example.gramride.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.gramride.entity.User;
import com.example.gramride.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Signup (User or Driver)
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ User already exists with email: " + user.getEmail());
        }

        user.setStatus("ACTIVE");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmailAndRole(
                loginRequest.getEmail(), loginRequest.getRole());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid email or role");
        }

        User user = userOpt.get();

        boolean isPasswordMatch = loginRequest.getRole().equalsIgnoreCase("USER")
                ? loginRequest.getPassword().equals(user.getPassword()) // Plaintext match for USER (not secure)
                : passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());

        if (!isPasswordMatch) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Invalid password");
        }

        // ✅ Return only safe user details (no password)
        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "fullName", user.getFullName(),
            "email", user.getEmail(),
            "role", user.getRole().toUpperCase(),  // 👈 ensure role is always uppercase
            "status", user.getStatus()
        ));
    }


    // 👥 Get all users
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🔍 Get users by role
    @GetMapping("/by-role/{role}")
    public List<User> getUsersByRole(@PathVariable String role) {
        return userRepository.findByRoleIgnoreCase(role);
    }

    // 🚗 Get all drivers
    @GetMapping("/drivers")
    public List<User> getAllDrivers() {
        return userRepository.findByRole("DRIVER");
    }

    // ➕ Add driver (admin action)
    @PostMapping("/addDriver")
    public ResponseEntity<?> addDriver(@RequestBody User driver) {
        if (userRepository.findByEmail(driver.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Driver already exists");
        }

        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        driver.setRole("DRIVER");
        driver.setStatus("ACTIVE");

        return ResponseEntity.ok(userRepository.save(driver));
    }

    // 🚫 Suspend driver
    @PutMapping("/suspend/{id}")
    public ResponseEntity<?> suspendDriver(@PathVariable Long id) {
        User driver = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus("SUSPENDED");
        userRepository.save(driver);
        return ResponseEntity.ok("✅ Driver suspended");
    }

    // 🔄 Update user status
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, String> payload) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(payload.get("status"));
        userRepository.save(user);
        return ResponseEntity.ok("✅ User status updated");
    }

    // 🚖 Get available drivers by string location (text-based, not lat/lng)
    @GetMapping("/available-drivers/{location}")
    public ResponseEntity<?> getAvailableDriversByLocation(@PathVariable String location) {
        List<User> drivers = userRepository.findByRoleAndStatusAndAvailableAndCurrentLocationIgnoreCase(
                "DRIVER", "ACTIVE", true, location);

        if (drivers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ No available drivers found at location: " + location);
        }

        return ResponseEntity.ok(drivers.get(0)); // Return first available
    }
}
