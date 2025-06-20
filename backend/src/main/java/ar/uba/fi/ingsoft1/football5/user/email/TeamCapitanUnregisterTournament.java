package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.cglib.core.Local;
import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TeamCapitanUnregisterTournament extends EmailWriter {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String tournamentName;

    public TeamCapitanUnregisterTournament(LocalDate startDate, LocalDate endDate, String tournamentName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.tournamentName = tournamentName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your team has been unregistered from a tournament");

        message.setText("Hello Team Captain,\n\n"
                + "This is to confirm that your team has been successfully unregistered from the tournament.\n\n"
                + "Tournament Details:\n"
                + "Name: " + tournamentName + "\n"
                + "Start Date: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "End Date: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n"
                + "If this was not intended, you may re-register your team while the registration window is still open.\n\n"
                + "Best regards,\n"
                + "PartidosYa Team");
    }
}