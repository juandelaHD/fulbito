package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public void sendMailToVerifyAccount(String recipientEmailAddress, String token) {
        this.sendMail(recipientEmailAddress, new AccountVerificationMailWriter(token));
    }

    public void sendPasswordResetMail(String recipientEmailAddress, String token) {
        this.sendMail(recipientEmailAddress, new PasswordResetMailWriter(token));
    }

    public void sendPasswordChangedMail(String recipientEmailAddress) {
        this.sendMail(recipientEmailAddress, new PasswordChangeMailWriter());
    }

    public void sendReservationMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchReservationMailWriter(date, start, end));
    }

    public void sendMatchNewReservationMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end, String fieldName) {
        this.sendMail(recipientEmail, new MatchNewReservationMailWriter(date, start, end, fieldName));
    }

    public void sendTeamCaptainMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end, String OrganizerUsername) {
        this.sendMail(recipientEmail, new TeamCaptainMatchMailWriter(date, start, end, OrganizerUsername));
    }

    public void sendTeamAssignmentMail(String recipientEmail, String teamName, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new TeamAssignmentMailWriter(teamName, date, start, end));
    }

    public void sendUnsubscribeMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new UnsubscribeMailWriter(date, start, end));
    }

    public void sendReservationConfirmedMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchReservationConfirmedMailWriter(date, start, end));
    }

    public void sendMatchFinishedMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchFinishedMailWriter(date, start, end));
    }

    public void sendMatchCancelledMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchCancelledMailWriter(date, start, end));
    }

}
