package com.example.demo.controllers;

import com.example.demo.dto.CurrentAccess;
import com.example.demo.dto.RoleChange;
import com.example.demo.dto.UserOption;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.PatientAccessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccessController {

    private final PatientAccessService access;

    public AccessController(PatientAccessService access) {
        this.access = access;
    }

    /** The signed-in user and their role. */
    @GetMapping("/access/me")
    public CurrentAccess me() {
        User u = access.currentUser();
        Role role = access.roleOf(u);
        return new CurrentAccess(u.getId(), u.displayName(), u.getEmail(), role, role == Role.ADMIN);
    }

    /** Admin only: everyone who can be chosen as a case worker. */
    @GetMapping("/users/case-workers")
    public List<UserOption> caseWorkers() {
        access.requireAdmin();
        return access.caseWorkerChoices().stream()
                .map(u -> new UserOption(u.getId(), u.displayName(), u.getEmail(), access.roleOf(u)))
                .toList();
    }

    /** Admin only: make a user an admin or staff. */
    @PutMapping("/users/{id}/role")
    @Transactional
    public UserOption changeRole(@PathVariable Long id, @Valid @RequestBody RoleChange body) {
        access.requireAdmin();
        User target = access.findUser(id);
        if (target.getId().equals(access.currentUser().getId()) && body.role() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove your own admin access.");
        }
        target.setRole(body.role());
        return new UserOption(target.getId(), target.displayName(), target.getEmail(), access.roleOf(target));
    }
}
