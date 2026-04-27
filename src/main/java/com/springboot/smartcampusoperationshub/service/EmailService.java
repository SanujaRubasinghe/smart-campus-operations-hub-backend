package com.springboot.smartcampusoperationshub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            javaMailSender.send(message);
            log.info("Plain text email successfully sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send plain text email to {}", to, e);
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, java.util.Map<String, Object> variables) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }
            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content

            javaMailSender.send(message);
            log.info("HTML email successfully sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}", to, e);
        }
    }
}
