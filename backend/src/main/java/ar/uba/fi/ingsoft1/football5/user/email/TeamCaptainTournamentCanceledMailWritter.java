package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TeamCaptainTournamentCanceledMailWritter extends EmailWriter {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String tournamentName;

    public TeamCaptainTournamentCanceledMailWritter(LocalDate startDate, LocalDate endDate, String tournamentName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.tournamentName = tournamentName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Tournament cancellation notice");

        message.setText("Hello Team Captain,\n\n"
                + "We regret to inform you that the tournament your team was registered in has been cancelled.\n\n"
                + "Tournament Details:\n"
                + "Name: " + tournamentName + "\n"
                + "Original Start Date: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Original End Date: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n"
                + "We apologize for any inconvenience this may cause. No matches will be held, and all tournament activity has been suspended.\n\n"
                + "Thank you for your understanding.\n\n"
                + "Best regards,\n"
                + "PartidosYa Team");
    }
}
