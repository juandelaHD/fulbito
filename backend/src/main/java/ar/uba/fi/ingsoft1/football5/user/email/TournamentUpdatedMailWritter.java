package ar.uba.fi.ingsoft1.football5.user.email;

import org.springframework.mail.SimpleMailMessage;

import ar.uba.fi.ingsoft1.football5.tournaments.Tournament;

import java.time.format.DateTimeFormatter;

public class TournamentUpdatedMailWritter extends EmailWriter {
    private final Tournament tournament;

    public TournamentUpdatedMailWritter(Tournament tournament) {
        this.tournament = tournament;
    }

    @Override
    public void writeMessage(SimpleMailMessage message) {
        message.setSubject("Your tournament has been updated");

        message.setText("Hello " + tournament.getOrganizer() + ",\n\n"
                + "This is to confirm that the details of your tournament have been successfully updated.\n\n"
                + "Updated Tournament Summary:\n"
                + "Name: " + tournament.getName() + "\n"
                + "Start Date: " + tournament.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "End Date: " + tournament.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n"
                + "Format: " + tournament.getFormat().name() + "\n"
                + "Max Teams: " + tournament.getMaxTeams() + "\n"
                + "Rules: " + tournament.getRules() + "\n"
                + "Prizes: " + tournament.getPrizes() + "\n"
                + "Registration Fee: $" + tournament.getRegistrationFee() + "\n\n"
                + "You can continue managing your tournament through your organizer dashboard.\n\n"
                + "Thank you for using PartidosYa!\n\n"
                + "Best regards,\n"
                + "PartidosYa Team");
    }
}
