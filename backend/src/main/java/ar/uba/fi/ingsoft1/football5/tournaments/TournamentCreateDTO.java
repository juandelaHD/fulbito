package ar.uba.fi.ingsoft1.football5.tournaments;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;

public class TournamentCreateDTO {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank
    private String format;

    @NotNull
    private Integer maxTeams;

    private String rules;
    private String prizes;
    private BigDecimal registrationFee;

    public String getName(){
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getFormat(){
        return format;
    }

    public Integer getMaxTeams(){
        return maxTeams;
    }

    public String getRules(){
        return rules;
    }

    public String getPrizes(){
        return prizes;
    }

    public BigDecimal getRegistrationFee(){
        return registrationFee;
    }

}