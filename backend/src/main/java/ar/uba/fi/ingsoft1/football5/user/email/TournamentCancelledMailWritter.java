package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TournamentCancelledMailWritter extends EmailWriter {
    private final String organizerUsername;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String tournamentName;

    public TournamentCancelledMailWritter(String organizerUsername, LocalDate startDate, LocalDate endDate, String tournamentName) {
        this.organizerUsername = organizerUsername;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tournamentName = tournamentName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your tournament has been cancelled");

        message.setText("Hello " + organizerUsername + ",\n\n"
                + "We confirm that your tournament has been successfully cancelled.\n\n"
                + "Tournament Details:\n"
                + "Name: " + tournamentName + "\n"
                + "Original Start Date: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Original End Date: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n"
                + "All registered teams have been notified about the cancellation.\n\n"
                + "Thank you for organizing with PartidosYa. We hope to see your next tournament soon.\n\n"
                + "Best regards,\n"
                + "PartidosYa Team");
    }
}