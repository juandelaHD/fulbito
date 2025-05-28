package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    private final String senderEmailAddress;

    public EmailSenderService(JavaMailSender mailSender, @Value("${spring.mail.username}") String senderEmailAddress) {
        this.mailSender = mailSender;
        this.senderEmailAddress = senderEmailAddress;
    }

    public void sendMail(String recipientEmailAddress, EmailWriter mailType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.senderEmailAddress);
        message.setTo(recipientEmailAddress);
        mailType.writeMessage(message);
        mailSender.send(message);
    }

    /*
    public void sendPasswordChangedMail(String recipientEmailAddress) {
        this.sendMail(recipientEmailAddress, new PasswordChangeMailWriter());
    }
    */

    public void sendMailToVerifyAccount(String recipientEmailAddress, String token) {
        this.sendMail(recipientEmailAddress, new AccountVerificationMailWriter(token));
    }
}
