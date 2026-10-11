package com.example.demo.controllers;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationCookieService;
import com.example.demo.service.EmailService;
import com.example.demo.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${app.security.password-reset-expiry-minutes}")
    private int passwordResetExpiryMinutes;

  

    @Value("${app.url:https://localhost:8080/}")
    private String frontendUrl;

    private final AuthenticationCookieService authenticationCookieService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    // ====================== REGISTER ======================
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .findFirst()
                    .orElse("Validation failed");

            return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
        }

        String email = request.email().toLowerCase().trim();
        String password = request.password();
        String username = request.name().trim();
        

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "An account with that email already exists"));
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username(username)
                .emailVerified(false)
                .build();

        userRepository.save(newUser);

        //String verificationToken = jwtService.generateVerificationToken(newUser);
        //String verificationUrl = "https://senami-1.onrender.com/auth/verify-email?token=" + verificationToken;

        //emailService.sendVerificationEmail(email, username, verificationUrl);

        return ResponseEntity.ok(Map.of(
                "message", "Registration successful. Please check your email to verify your account."
        ));
    }

    // ====================== VERIFY EMAIL ======================
@GetMapping("/verify-email")
public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {

    String email = jwtService.validateVerificationTokenAndGetEmail(token);

    User user = userRepository.findByEmail(email).orElseThrow();

    user.setEmailVerified(true);
    userRepository.save(user);

    

    response.sendRedirect("/pricing.html?verified=true");
}

    // ====================== LOGIN ======================
   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body,
                               HttpServletResponse response) {
    
    String email = body.get("email");
    String password = body.get("password");

    // Input validation
    if (email == null || email.isBlank()) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Email is required"));
    }
    if (password == null || password.isBlank()) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Password is required"));
    }

    email = email.toLowerCase().trim();

    // Find user
    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
        log.warn("Login failed - Invalid credentials for email: {}", email);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password"));
    }

    // Enforce email verification
    if (!user.isEmailVerified()) {
        log.warn("Login failed - Email not verified: {}", email);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Please verify your email before logging in"));
    }

    // Set authentication cookies (JWT + Refresh Token)
    try {
        authenticationCookieService.loginUser(user, response);
        log.info("Login successful for user: {}", email);
    } catch (Exception e) {
        log.error("Failed to set authentication cookies for user: {}", email, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Login failed. Please try again."));
    }

    // Return user data
    return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "id", user.getId(),
            "email", user.getEmail(),
            "username", user.getUsername() != null ? user.getUsername() : ""
            
            
    ));
}

    // ====================== ME (with proper logging) ======================
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        log.info("/me endpoint reached");

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            log.warn("/me - Unauthorized access attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        String email = authentication.getName();
        log.info("/me - User email: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.error("/me - User not found for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }

        log.info("/me - Success for user: {}", email);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "username", user.getUsername() != null ? user.getUsername() : ""
        ));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authenticationCookieService.clearUser(response);
        response.addCookie(authenticationCookieService.deleteCookie(response, "access_token", "/"));
        response.addCookie(authenticationCookieService.deleteCookie(response, "refresh_token", "/"));
      
        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully"
        ));
    }

      /* ====================== FORGOT PASSWORD ====================== */


    @PostMapping("/forgot-password")
public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
    String email = body.get("email");

    if (email == null || email.isBlank()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
    }

    String normalizedEmail = email.toLowerCase().trim();

    // Find user
    User user = userRepository.findByEmail(normalizedEmail).orElse(null);

    if (user == null) {
        return ResponseEntity.badRequest().body(Map.of("error", "No account found with that email"));
    }

    if (!user.isEmailVerified()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Email not verified"));
    }

    // Generate reset token
    String resetToken = UUID.randomUUID().toString();

    user.setPasswordResetToken(resetToken);
    user.setPasswordResetExpires(LocalDateTime.now().plusHours(1));
    
    userRepository.save(user);

    // Create reset URL
    String resetUrl = "https://localhost:8080/reset-password.html?token=" + resetToken;

    // Send email
    emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

    return ResponseEntity.ok(Map.of(
            "message", "Password reset link has been sent to your email"
    ));
}


@PostMapping("/reset-password")
public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
    String token = body.get("token");
    String newPassword = body.get("password");

    if (token == null || token.isBlank()) {
        return ResponseEntity.badRequest().body(Map.of("error", "Reset token is required"));
    }
    if (newPassword == null || newPassword.length() < 8) {
        return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 8 characters"));
    }

    return userRepository.findByPasswordResetToken(token)
            .filter(user -> user.getPasswordResetExpires() != null           // ← Fixed
                    && user.getPasswordResetExpires().isAfter(LocalDateTime.now()))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setPasswordResetToken(null);      // ← This one was correct
                user.setPasswordResetExpires(null);    // ← Fixed
                userRepository.save(user);

                return ResponseEntity.ok(Map.of(
                        "message", "Password successfully reset. You can now log in."
                ));
            })
            .orElse(ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid or expired reset token"
            )));
        }

     @PostMapping("/refresh")
public ResponseEntity<?> refreshToken(
        @CookieValue(name = "refresh_token", required = false) String refreshToken,
        HttpServletResponse response
) {
    if (refreshToken == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "No refresh token"
        ));
    }

    try {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid token type");
        }

        String email = jwtService.getEmailFromToken(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Issue NEW tokens
        authenticationCookieService.loginUser(user, response);

        return ResponseEntity.ok(Map.of(
                "message", "Token refreshed"
        ));

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Invalid refresh token"
        ));
    }
}

@GetMapping("/test123")
public String test123() {
    return "alive";
}
}