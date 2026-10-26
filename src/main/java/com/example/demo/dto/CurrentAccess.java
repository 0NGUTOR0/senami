package com.example.demo.dto;

import com.example.demo.model.Role;

/** Who is signed in and what they may do, so the pages can show or hide admin controls. */
public record CurrentAccess(Long id, String name, String email, Role role, boolean admin) {
}
