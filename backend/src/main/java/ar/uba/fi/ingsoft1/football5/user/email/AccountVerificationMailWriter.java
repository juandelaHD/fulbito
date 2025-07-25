package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

public class AccountVerificationMailWriter extends EmailWriter {
    private final String token;

    public AccountVerificationMailWriter(String token) {
        this.token = token;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Account Verification");
        message.setText("Hi there,\n\n"
                + "Welcome to our football community! ⚽\n\n"
                + "Thanks for signing up. To complete your registration, please confirm your email by clicking the link below:\n\n"
                + "http://localhost:30003/verify-email?token=" + token + "\n\n"
                + "Once confirmed, you’ll be able to explore players, share your opinions, and be part of the passion of football in Argentina.\n\n"
                + "See you on the pitch!\n"
                + "— PartidosYa");
    }
}