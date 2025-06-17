package ar.uba.fi.ingsoft1.football5.tournaments;

import ar.uba.fi.ingsoft1.football5.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference("organizer-match")
    private User organizer;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private String format; 

    @Column(nullable = false)
    private Integer maxTeams;

    @Column(length = 10000) 
    private String rules;

    private String prizes;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal registrationFee;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status = TournamentStatus.OPEN_FOR_REGISTRATION;

    protected Tournament(){}

    public Tournament(String name, User organizer, LocalDate startDate, LocalDate endDate, String format, Integer maxTeams, String rules, String prizes, BigDecimal registrationFee) {
        this.name = name;
        this.organizer = organizer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.format = format;
        this.maxTeams = maxTeams;
        this.rules = rules;
        this.prizes = prizes;
        this.registrationFee = registrationFee;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public User getOrganizer(){
        return organizer;
    }

    public void setOrganizer(User organizer){
        this.organizer = organizer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startTime) {
        this.startDate = startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getFormat(){
        return format;
    }

    public void setFormat(String format){
        this.format = format;
    }

    public Integer getMaxTeams(){
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams){
        this.maxTeams = maxTeams;
    }

    public String getRules(){
        return rules;
    }

    public void setRules(String rules){
        this.rules = rules;
    }

    public String getPrizes(){
        return prizes;
    }

    public void setPrizes(String prizes){
        this.prizes = prizes;
    }

    public BigDecimal getRegistrationFee(){
        return registrationFee;
    }

    public void setRegistrationFee(BigDecimal registrationFee){
        this.registrationFee = registrationFee;
    }

    public TournamentStatus getStatus(){
        return status;
    }

    public void setStatus(TournamentStatus status){
        this.status = status;
    }

}