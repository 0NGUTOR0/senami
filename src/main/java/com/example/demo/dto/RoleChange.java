package com.example.demo.dto;

import com.example.demo.model.Role;
import jakarta.validation.constraints.NotNull;

public record RoleChange(@NotNull Role role) {
}
