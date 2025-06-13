package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchFinishedMailWriter extends EmailWriter {
    private final LocalDate date;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public MatchFinishedMailWriter(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your match has finished!");
        message.setText("Hi there,\n\n"
                + "The match scheduled for: " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n\n"
                + "Time:\n"
                + "- From: " + startDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n"
                + "- To: " + endDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n\n"
                + "has finished.\n\n"
                + "Thank you for playing with us!\n\n"
                + "— PartidosYa");
    }
}