package ar.uba.fi.ingsoft1.football5.user.email;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;

public class MatchVerificationMailWriter extends EmailWriter {
    private final String token;

    private final LocalDate date;

    private final LocalDateTime startDate;

    private final LocalDateTime endDate;

    public MatchVerificationMailWriter(String token,LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        this.token = token;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Match Verification");
        message.setText("Hi there,\n\n"
                + "A match is scheduled in the following day:"+ date.format(DateTimeFormatter.ISO_LOCAL_DATE) +"\n\n"
                + "Start Date: " + startDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n\n"
                + "End Date: " + endDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n\n"
                + "To conclude the set up please click the link below:\n\n"
                + "http://localhost:30002/matches/verify-email?token=" + token + "\n\n"
                + "Be prepared!\n"
                + "— Ing. de Software I - Group 8");
    }
}
