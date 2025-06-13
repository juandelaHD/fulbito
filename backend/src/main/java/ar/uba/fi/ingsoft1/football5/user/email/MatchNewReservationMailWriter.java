package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchNewReservationMailWriter extends EmailWriter {
    private final LocalDate date;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String fieldName;

    public MatchNewReservationMailWriter(LocalDate date, LocalDateTime startDate, LocalDateTime endDate, String fieldName) {
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fieldName = fieldName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("New reservation request for your field: " + fieldName);
        message.setText("Hello,\n\n"
                + "A new match reservation has been requested for your field: " + fieldName + "\n"
                + "Date: " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n"
                + "Time:\n"
                + "- From: " + startDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n"
                + "- To: " + endDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n\n"
                + "Please confirm or reject this reservation in the system.\n\n"
                + "— PartidosYa");
    }
}