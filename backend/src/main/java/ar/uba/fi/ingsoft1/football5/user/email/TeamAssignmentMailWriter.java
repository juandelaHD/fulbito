package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TeamAssignmentMailWriter extends EmailWriter {
    private final String teamName;
    private final LocalDate date;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public TeamAssignmentMailWriter(String teamName, LocalDate date, LocalDateTime startDate, LocalDateTime endDate) {
        this.teamName = teamName;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your match teams have been formed!");
        message.setText("Hello,\n\n"
                + "The teams for your match on " + date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " have been formed.\n"
                + "You will play in: " + teamName + "\n"
                + "Time: " + startDate.format(DateTimeFormatter.ofPattern("HH:mm")) + " to " + endDate.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
                + "Check the app for more details.\n\n"
                + "Good luck!\n"
                + "— PartidosYa");
    }
}