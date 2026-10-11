package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection (recommended)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find a user by their ID
     * 
     * @param id the user's ID
     * @return the User entity
     * @throws UsernameNotFoundException if user is not found
     */
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with id: " + id));
    }

    /**
     * Get the currently authenticated user from the SecurityContext
     * 
     * @param authentication the current Authentication object (injected by Spring Security)
     * @return the authenticated User entity
     * @throws UsernameNotFoundException if the authenticated user cannot be found in DB
     */
    public User getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        System.out.println("getCurrentUser: No authentication");
        return null;
    }

    String email = authentication.getName();
    System.out.println("getCurrentUser: Trying to find user with email = " + email);

    if (email == null || email.isBlank()) {
        return null;
    }

    return userRepository.findByEmail(email.toLowerCase().trim())
            .orElse(null);   // Return null instead of throwing
}}