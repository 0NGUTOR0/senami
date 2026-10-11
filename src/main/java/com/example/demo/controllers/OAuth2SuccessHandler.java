package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuthenticationCookieService authenticationCookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String rawEmail = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        if (rawEmail == null) {
            response.sendRedirect("/index.html?error=email_not_found");
            return;
        }

        String email = rawEmail.toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewOAuthUser(email, name));

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        authenticationCookieService.loginUser(user, response);
        response.sendRedirect("/index.html");
    }

    private User createNewOAuthUser(String email, String name) {
        User newUser = User.builder()
                .email(email)
                .username(name != null ? name : email.split("@")[0])
                .password("")
                .emailVerified(true)
                .build();

        return userRepository.save(newUser);
    }
}