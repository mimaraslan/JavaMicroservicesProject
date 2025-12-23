package com.mimaraslan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailConfig {

    private String host;
    private int port;
    private String username;
    private String password;

    // Setterlar Spring Boot tarafından config.yml’den set edilecek
    public void setHost(String host) { this.host = host; }
    public void setPort(int port) { this.port = port; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // prod için false yapabilirsin

        return mailSender;
    }
}
