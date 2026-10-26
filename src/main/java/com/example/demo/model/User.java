package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;               // hashed, required for login & future upgrades

    private String username;               // optional

    /**
     * ADMIN or STAFF. New accounts are STAFF. Rows created before this column existed are null,
     * and are treated as STAFF too (the least access).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    @Builder.Default
    private Role role = Role.STAFF;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = true;  // we trust Stripe-provided email

    // Password reset fields (keep if you have reset flow)
    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;

    /** The name shown for this user in lists: the username, or the email when there is none. */
    public String displayName() {
        return username != null && !username.isBlank() ? username : email;
    }
}
