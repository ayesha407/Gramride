package com.example.gramride.repository;

import com.example.gramride.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndPasswordAndRole(String email, String password, String role);
    List<User> findByRole(String role);
    List<User> findByRoleAndStatus(String role, String status);
    List<User> findByRoleIgnoreCase(String role);
    Optional<User> findByEmailAndRole(String email, String role);
    List<User> findByRoleAndStatusAndAvailableAndCurrentLocationIgnoreCase(String role, String status, boolean available, String location);
    Optional<User> findTopByRoleAndStatusAndAvailableTrue(String role, String status);



}
