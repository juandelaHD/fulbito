package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void sendMailToVerifyAccount(String recipientEmailAddress, String token) {
        this.sendMail(recipientEmailAddress, new AccountVerificationMailWriter(token));
    }

    @Async
    public void sendPasswordResetMail(String recipientEmailAddress, String token) {
        this.sendMail(recipientEmailAddress, new PasswordResetMailWriter(token));
    }

    @Async
    public void sendPasswordChangedMail(String recipientEmailAddress) {
        this.sendMail(recipientEmailAddress, new PasswordChangeMailWriter());
    }

    @Async
    public void sendReservationMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchReservationMailWriter(date, start, end));
    }

    @Async
    public void sendMatchNewReservationMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end, String fieldName) {
        this.sendMail(recipientEmail, new MatchNewReservationMailWriter(date, start, end, fieldName));
    }

    @Async
    public void sendTeamCaptainMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end, String OrganizerUsername) {
        this.sendMail(recipientEmail, new TeamCaptainMatchMailWriter(date, start, end, OrganizerUsername));
    }

    @Async
    public void sendTeamAssignmentMail(String recipientEmail, String teamName, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new TeamAssignmentMailWriter(teamName, date, start, end));
    }

    @Async
    public void sendUnsubscribeMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new UnsubscribeMailWriter(date, start, end));
    }

    @Async
    public void sendReservationConfirmedMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchReservationConfirmedMailWriter(date, start, end));
    }

    @Async
    public void sendMatchFinishedMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchFinishedMailWriter(date, start, end));
    }

    @Async
    public void sendMatchCancelledMail(String recipientEmail, LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.sendMail(recipientEmail, new MatchCancelledMailWriter(date, start, end));
    }

    private void sendMail(String recipientEmailAddress, EmailWriter mailType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.senderEmailAddress);
        message.setTo(recipientEmailAddress);
        mailType.writeMessage(message);
        mailSender.send(message);
    }
}
