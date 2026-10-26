package com.example.demo.service;

import com.example.demo.model.Patient;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Who may see which patient. Admins see everyone; staff only see patients they are the case worker for.
 * A patient a staff member may not see is reported as "not found", never as "forbidden".
 */
@Service
public class PatientAccessService {

    private final CurrentUserService currentUserService;
    private final Set<String> adminEmails;

    @PersistenceContext
    private EntityManager em;

    /**
     * app.admin-emails (environment variable APP_ADMIN_EMAILS) is a comma-separated list of emails that are always admins.
     * It is how the first admin is created, because every new account starts as STAFF.
     */
    public PatientAccessService(CurrentUserService currentUserService,
                                @Value("${app.admin-emails:}") String adminEmails) {
        this.currentUserService = currentUserService;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(s -> s.trim().toLowerCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    public User currentUser() {
        return currentUserService.getCurrentUser();
    }

    public Role roleOf(User user) {
        if (user.getEmail() != null && adminEmails.contains(user.getEmail().trim().toLowerCase(Locale.ROOT))) {
            return Role.ADMIN;
        }
        return user.getRole() == null ? Role.STAFF : user.getRole();
    }

    public boolean isAdmin() {
        return roleOf(currentUser()) == Role.ADMIN;
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an admin can do this.");
        }
    }

    public boolean canAccess(Patient patient) {
        User me = currentUser();
        if (roleOf(me) == Role.ADMIN) return true;
        return patient.getCaseWorker() != null && patient.getCaseWorker().getId().equals(me.getId());
    }

    public void requireAccess(Patient patient) {
        if (!canAccess(patient)) {
            throw new PatientNotFoundException("Patient not found: " + patient.getId());
        }
    }

    /** Everyone who can be chosen as a case worker. */
    @Transactional(readOnly = true)
    public List<User> caseWorkerChoices() {
        return em.createQuery("select u from User u order by lower(coalesce(u.username, u.email))", User.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public User findUser(Long id) {
        User user = id == null ? null : em.find(User.class, id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user: " + id);
        }
        return user;
    }
}
