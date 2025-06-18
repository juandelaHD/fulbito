package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TournamentCreatedMailWriter extends EmailWriter {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String organizerUsername;
    private final String tournamentName;

    public TournamentCreatedMailWriter(LocalDate startDate, LocalDate endDate, String organizerUsername, String tournamentName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizerUsername = organizerUsername;
        this.tournamentName = tournamentName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your tournament has been successfully created!");

        message.setText("Hello " + organizerUsername + ",\n\n"
                + "Congratulations! Your tournament has been successfully created and is now open for team registrations.\n\n"
                + "Tournament Details:\n"
                + "Name: " + tournamentName + "\n"
                + "Start Date: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "End Date: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n"
                + "You can track registrations and manage the tournament from your organizer panel.\n\n"
                + "Thank you for using PartidosYa!\n\n"
                + "Best regards,\n"
                + "PartidosYa Team");
    }
}
