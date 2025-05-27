package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    private final String senderEmailAddress = "appfulbito.manager@gmail.com";

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
