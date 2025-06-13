package ar.uba.fi.ingsoft1.football5.user.email;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UnsubscribeMailWriter extends EmailWriter {
    private final LocalDate date;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public UnsubscribeMailWriter(LocalDate date, LocalDateTime start, LocalDateTime end) {
        this.date = date;
        this.start = start;
        this.end = end;
    }

    @Override
    protected String getSubject() {
        return "Unsubscription from football match confirmed";
    }

    @Override
    protected String getTextContent() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return "You have successfully unsubscribed from the football match scheduled on "
                + date.format(dateFormatter)
                + " from " + start.format(timeFormatter)
                + " to " + end.format(timeFormatter)
                + ".\n\nYour spot has been released.\n\nThank you for letting us know!";
    }
}