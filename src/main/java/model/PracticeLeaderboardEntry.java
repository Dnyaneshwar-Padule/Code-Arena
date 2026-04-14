package model;

public class PracticeLeaderboardEntry {

    private Long userId;
    private String username;
    private Integer points;
    private Integer solvedCount;
    private Integer rank;

    public PracticeLeaderboardEntry() {
    }

    public PracticeLeaderboardEntry(Long userId, String username, Integer points, Integer solvedCount, Integer rank) {
        this.userId = userId;
        this.username = username;
        this.points = points;
        this.solvedCount = solvedCount;
        this.rank = rank;
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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getSolvedCount() {
        return solvedCount;
    }

    public void setSolvedCount(Integer solvedCount) {
        this.solvedCount = solvedCount;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
