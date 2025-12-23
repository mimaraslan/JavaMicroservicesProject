package com.mimaraslan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mimaraslan.dto.TransferRequest;
import com.mimaraslan.dto.AccountResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
public class NotificationService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String gatewayToken;

    public NotificationService(
            ObjectMapper objectMapper,
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            @Value("${notification.gateway.base-url:http://localhost:80}") String gatewayBaseUrl,
            @Value("${notification.gateway.token:}") String gatewayToken
    ) {
        this.objectMapper = objectMapper;
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
        this.gatewayToken = StringUtils.hasText(gatewayToken) ? toBearer(gatewayToken) : null;

        WebClient.Builder builder = WebClient.builder()
                // Account bilgilerini gateway üzerinden çekiyoruz
                .baseUrl(gatewayBaseUrl + "/account/");

        if (this.gatewayToken != null) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, this.gatewayToken);
        } else {
            log.warn("notification.gateway.token is empty; WebClient calls will be unauthenticated");
        }

        this.webClient = builder.build();
    }

    @Async
    public void sendNotification(String message) {

        try {
            TransferRequest transfer = objectMapper.readValue(message, TransferRequest.class);

            log.info("[NotificationService] Transfer event received: from={} to={} amount={} fromAccountId={} toAccountId={}",
                    transfer.getFromAccount(),
                    transfer.getToAccount(),
                    transfer.getAmount(),
                    transfer.getFromAccountId(),
                    transfer.getToAccountId()
            );

            Long fromAccountId = transfer.getFromAccountId();
            Long toAccountId   = transfer.getToAccountId();

            log.debug("Processing notification: transferId={}, fromAccountId={}, toAccountId={}, hasToken={}", 
                    transfer.getTransferId(), fromAccountId, toAccountId, 
                    transfer.getAuthToken() != null && !transfer.getAuthToken().isEmpty());

            // Gönderen kullanıcı bilgisi
            AccountResponse fromAccountInfo = webClient.get()
                    .uri(fromAccountId.toString())
                    .headers(headers -> applyAuth(headers, transfer.getAuthToken()))
                    .retrieve()
                    .bodyToMono(AccountResponse.class)
                    .block();

            log.debug("Retrieved from account info: accountId={}, email={}", 
                    fromAccountId, fromAccountInfo != null ? fromAccountInfo.getEmail() : "null");
            // Alıcı kullanıcı bilgisi
            AccountResponse toAccountInfo = webClient.get()
                    .uri(toAccountId.toString())
                .headers(headers -> applyAuth(headers, transfer.getAuthToken()))
                    .retrieve()
                    .bodyToMono(AccountResponse.class)
                    .block();

            // Gönderen mail (çıkış)
            if (fromAccountInfo != null) {
                String html = createEmailTemplate(
                        fromAccountInfo.getFirstName() + " " + fromAccountInfo.getLastName(),
                        transfer.getAmount().toString(),
                        String.valueOf(transfer.getFromAccount()),
                        "Para Çıkışı",
                        fromAccountInfo, // Gönderen bilgileri
                        toAccountInfo    // Alıcı bilgileri
                );
                sendHtmlMail(fromAccountInfo.getEmail(), "Transfer Bildirimi", html);
            }

            // Alıcı mail (giriş)
            if (toAccountInfo != null) {
                String html = createEmailTemplate(
                        toAccountInfo.getFirstName() + " " + toAccountInfo.getLastName(),
                        transfer.getAmount().toString(),
                        String.valueOf(transfer.getToAccount()),
                        "Para Girişi",
                        fromAccountInfo, // Gönderen bilgileri
                        toAccountInfo    // Alıcı bilgileri
                );
                sendHtmlMail(toAccountInfo.getEmail(), "Transfer Bildirimi", html);
            }

        } catch (Exception e) {
            log.error("NotificationService ERROR:", e);
            throw new RuntimeException("Unexpected notification error", e);
        }

    }

    private String toBearer(String token) {
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    private void applyAuth(HttpHeaders headers, String tokenFromMessage) {
        String token = StringUtils.hasText(tokenFromMessage)
                ? toBearer(tokenFromMessage)
                : gatewayToken;
        if (token != null) {
            headers.set(HttpHeaders.AUTHORIZATION, token);
        }
    }

    private String createEmailTemplate(String fullName, String amount, String iban, String type, 
                                       AccountResponse senderInfo, AccountResponse receiverInfo) {
        Context context = new Context();
        
        // Tarih ve saat bilgilerini ekle
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        
        // Referans numarası oluştur (timestamp + random)
        String referenceNumber = "TRF-" + System.currentTimeMillis() + "-" + 
                java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        context.setVariable("fullName", fullName);
        context.setVariable("amount", amount);
        context.setVariable("iban", iban);
        context.setVariable("type", type);
        context.setVariable("transactionDate", now.format(dateFormatter));
        context.setVariable("transactionTime", now.format(timeFormatter));
        context.setVariable("referenceNumber", referenceNumber);
        
        // Gönderen bilgileri
        if (senderInfo != null) {
            context.setVariable("senderName", senderInfo.getFirstName() + " " + senderInfo.getLastName());
            context.setVariable("senderEmail", senderInfo.getEmail() != null ? senderInfo.getEmail() : "");
            context.setVariable("senderPhone", senderInfo.getPhoneNumber() != null ? senderInfo.getPhoneNumber() : "");
            context.setVariable("senderAddress", senderInfo.getAddress() != null ? senderInfo.getAddress() : "");
        } else {
            context.setVariable("senderName", "");
            context.setVariable("senderEmail", "");
            context.setVariable("senderPhone", "");
            context.setVariable("senderAddress", "");
        }
        
        // Alıcı bilgileri
        if (receiverInfo != null) {
            context.setVariable("receiverName", receiverInfo.getFirstName() + " " + receiverInfo.getLastName());
            context.setVariable("receiverEmail", receiverInfo.getEmail() != null ? receiverInfo.getEmail() : "");
            context.setVariable("receiverPhone", receiverInfo.getPhoneNumber() != null ? receiverInfo.getPhoneNumber() : "");
            context.setVariable("receiverAddress", receiverInfo.getAddress() != null ? receiverInfo.getAddress() : "");
        } else {
            context.setVariable("receiverName", "");
            context.setVariable("receiverEmail", "");
            context.setVariable("receiverPhone", "");
            context.setVariable("receiverAddress", "");
        }

        return templateEngine.process("transfer-email", context); // transfer-email.html
    }

    private void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML mode

            mailSender.send(mimeMessage);

            log.info("HTML Mail sent to {}", to);

        } catch (Exception e) {
            log.error("sendHtmlMail ERROR", e);
        }
    }
//    private void sendEmail(String to, String subject, String body) {
//        SimpleMailMessage mail = new SimpleMailMessage();
//        mail.setTo(to);
//        mail.setSubject(subject);
//        mail.setText(body);
//        mailSender.send(mail);
//        log.info("Mail sent to {}", to);
//    }

}
