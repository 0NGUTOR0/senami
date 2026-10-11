package com.example.demo.service;

import com.example.demo.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationCookieService {

    private final JwtService jwtService;

    public void loginUser(User user, HttpServletResponse response) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Access Token
        addCookie(response, "access_token", accessToken, "/", 15 * 60);        // 15 minutes

        // Refresh Token
        addCookie(response, "refresh_token", refreshToken, "/", 7 * 24 * 60 * 60);

        log.info("✅ Auth cookies set successfully for user: {} | Tier: {}", 
                user.getEmail());
    }

    public void clearUser(HttpServletResponse response) {
        deleteCookie(response, "access_token", "/");
        deleteCookie(response, "refresh_token", "/");
        deleteCookie(response, "refresh_token", "/auth");
        deleteCookie(response, "refresh_token", "/auth/refresh");
    }

    public void addCookie(HttpServletResponse response, String name, String value, 
                         String path, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);           // Keep true in production for security
        cookie.setSecure(false);            // Set to true when using HTTPS in production
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }

    public Cookie deleteCookie(HttpServletResponse response, String name, String path) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return cookie;
    }
}