package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contests")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "contest")
    private List<ContestProblem> contestProblems = new ArrayList<>();

    @OneToMany(mappedBy = "contest")
    private List<ContestRegistration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "contest")
    private List<Leaderboard> leaderboardEntries = new ArrayList<>();

    public Contest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<ContestProblem> getContestProblems() {
        return contestProblems;
    }

    public void setContestProblems(List<ContestProblem> contestProblems) {
        this.contestProblems = contestProblems;
    }

    public List<ContestRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<ContestRegistration> registrations) {
        this.registrations = registrations;
    }

    public List<Leaderboard> getLeaderboardEntries() {
        return leaderboardEntries;
    }

    public void setLeaderboardEntries(List<Leaderboard> leaderboardEntries) {
        this.leaderboardEntries = leaderboardEntries;
    }
}
