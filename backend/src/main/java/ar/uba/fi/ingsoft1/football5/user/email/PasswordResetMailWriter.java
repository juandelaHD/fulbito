package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

public class PasswordResetMailWriter extends EmailWriter {
    private final String token;

    public PasswordResetMailWriter(String token) {
        this.token = token;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Password Reset");
        message.setText("Hello,\n\n"
                + "We received a request to reset your password.\n"
                + "To continue, please click the following link:\n\n"
                // TODO: Change this URL to the actual production URL (FRONTEND_URL)
                + "http://localhost:30003/reset-password?token=" + token + "\n\n"
                + "If you did not request this change, you can safely ignore this email.\n\n"
                + "Best regards,\n"
                + "— PartidosYa");
    }
}