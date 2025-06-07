package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

public class PasswordChangeMailWriter extends EmailWriter {
    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Password Updated");
        message.setText("Hello,\n\n" +
                "Your password has been successfully updated.\n\n" +
                "If you did not make this change, please contact support.\n\n" +
                "— PartidosYa");
    }
}