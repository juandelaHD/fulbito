package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TeamCaptainTournamentRegisterMailWriter extends EmailWriter {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String organizerUsername;
    private final String tournamentName;

    public TeamCaptainTournamentRegisterMailWriter(LocalDate startDate, LocalDate endDate, String organizerUsername, String tournamentName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizerUsername = organizerUsername;
        this.tournamentName = tournamentName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your team has been registered to a tournament!");
        message.setText("Hello Team Captain,\n\n"
                + "Great news! Your team has been successfully registered to a tournament.\n\n"
                + "Tournament Details:\n"
                + "Name: " + tournamentName + "\n"
                + "Start Date: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "End Date: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Organizer: " + organizerUsername + "\n\n"
                + "You will receive future notifications about the match schedule as the tournament progresses.\n"
                + "Make sure your team is ready to compete!\n\n"
                + "Best regards,\n"
                + "PartidosYa");
    }
}
