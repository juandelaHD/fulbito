package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchReservationConfirmedMailWriter extends EmailWriter {
    private final LocalDate date;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public MatchReservationConfirmedMailWriter(LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your match reservation has been confirmed!");
        message.setText("Hi there,\n\n"
                + "Your match reservation has been confirmed for: " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n\n"
                + "Time:\n"
                + "- From: " + startDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n"
                + "- To: " + endDate.format(DateTimeFormatter.ISO_LOCAL_TIME) + "\n\n"
                + "See you on the field!\n\n"
                + "— PartidosYa");
    }
}