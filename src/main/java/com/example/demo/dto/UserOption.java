package com.example.demo.dto;

import com.example.demo.model.Role;

/** A user as shown in the case worker dropdown. */
public record UserOption(Long id, String name, String email, Role role) {
}
