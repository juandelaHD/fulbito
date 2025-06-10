package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TeamCaptainMatchMailWriter extends EmailWriter {
    private final LocalDate date;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String OrganaizerUsername;

    public TeamCaptainMatchMailWriter(LocalDate date, LocalDateTime startDate, LocalDateTime endDate, String OrganaizerUsername) {
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.OrganaizerUsername = OrganaizerUsername;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your team has a new match scheduled!");
        message.setText("Hello Team Captain,\n\n"
                + "We are excited to inform you that your team has a new match scheduled!\n\n"
                + "Match Details:\n"
                + "Date: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Start Time: " + startDate.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n"
                + "End Time: " + endDate.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n\n"
                + "Organizer: " + OrganaizerUsername + "\n\n"
                + "Please make sure your team is prepared and ready to play.\n\n"
                + "Best regards,\n"
                + "PartidosYa");
    }
}