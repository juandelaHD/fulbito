package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TeamCaptainTournamentUpdatedMailWritter extends EmailWriter {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String tournamentName;

    public TeamCaptainTournamentUpdatedMailWritter(LocalDate startDate, LocalDate endDate, String tournamentName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.tournamentName = tournamentName;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("A tournament you are registered in has been updated");

        message.setText("Hello Team Captain,\n\n"
                + "We would like to inform you that the tournament your team is registered in has been updated.\n\n"
                + "Tournament Details:\n"
                + "Name: " + tournamentName + "\n"
                + "New Start Date: " + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "New End Date: " + endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n\n"
                + "We recommend you review the tournament rules and schedule to ensure your team is prepared for any changes.\n\n"
                + "Thank you for participating!\n\n"
                + "Best regards,\n"
                + "PartidosYa Team");
    }
}
