package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from-address:no-reply@demo.com}")
    private String fromAddress;

    @Value("${app.mail.from-name:demo}")
    private String fromName;

    /**
     * If false, we won't send emails; we will only log links (dev/test).
     * Set this to true in production.
     */
    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    public void sendVerificationEmail(String toEmail, String name, String verificationLink) {
        String subject = "Verify your email";
        String html = """
            <div style="font-family: Arial, sans-serif; line-height:1.5">
              <h2>Hi %s,</h2>
              <p>Welcome to demo. Please verify your email by clicking the button below:</p>
              <p>
                <a href="%s"
                   style="display:inline-block;padding:12px 18px;background:#111;color:#fff;text-decoration:none;border-radius:8px">
                   Verify Email
                </a>
              </p>
              <p>If the button doesn’t work, copy and paste this link:</p>
              <p><a href="%s">%s</a></p>
              <p>If you didn’t create an account, you can ignore this email.</p>
            </div>
            """.formatted(escape(name), verificationLink, verificationLink, verificationLink);

        sendEmailOrLog(toEmail, subject, html, verificationLink, "VERIFY");
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "Reset your password";
        String html = """
            <div style="font-family: Arial, sans-serif; line-height:1.5">
              <h2>Password reset</h2>
              <p>Click the button below to reset your password:</p>
              <p>
                <a href="%s"
                   style="display:inline-block;padding:12px 18px;background:#111;color:#fff;text-decoration:none;border-radius:8px">
                   Reset Password
                </a>
              </p>
              <p>If the button doesn’t work, copy and paste this link:</p>
              <p><a href="%s">%s</a></p>
              <p>If you didn’t request this, please ignore this email.</p>
            </div>
            """.formatted(resetLink, resetLink, resetLink);

        sendEmailOrLog(toEmail, subject, html, resetLink, "RESET");
    }

    private void sendEmailOrLog(String toEmail, String subject, String htmlBody, String link, String tag) {
        // Don’t leak sensitive links in production logs
        if (!mailEnabled) {
            log.info("MAIL_DISABLED [{}] to={}", tag, toEmail);
            log.info("LINK [{}] {}", tag, link);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            // Use a friendly From name if supported by your SMTP provider
            helper.setFrom(fromAddress, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML = true

            mailSender.send(mimeMessage);
            log.info("MAIL_SENT [{}] to={}", tag, toEmail);

        } catch (MessagingException e) {
    log.error("MAIL_FAILED [{}] to={} subject={}", tag, toEmail, subject, e);
    // DO NOT THROW — continue execution
} catch (Exception e) {
    log.error("MAIL_FAILED_UNEXPECTED [{}] to={}", tag, toEmail, e);
    // DO NOT THROW — continue execution
}
    }

    // Basic HTML escaping to avoid breaking templates
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
