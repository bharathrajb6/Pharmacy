package com.example.Pharmacy.service.impl;

import com.example.Pharmacy.exception.MedicationException;
import com.example.Pharmacy.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * Send email
     *
     * @param to
     * @param subject
     * @param body
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
            log.info("Email sent to: " + to);
        } catch (Exception exception) {
            log.error("Error sending email: " + exception.getMessage());
            throw new MedicationException(exception.getMessage());
        }
    }

}
