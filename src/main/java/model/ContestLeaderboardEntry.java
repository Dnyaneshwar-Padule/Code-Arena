package model;

public class ContestLeaderboardEntry {

    private Long userId;
    private String username;
    private Integer score;
    private Integer rank;

    public ContestLeaderboardEntry() {
    }

    public ContestLeaderboardEntry(Long userId, String username, Integer score) {
        this.userId = userId;
        this.username = username;
        this.score = score;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
