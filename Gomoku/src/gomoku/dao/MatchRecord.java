package gomoku.dao;

public class MatchRecord {
    private int djh;
    private String res;
    private int change;
    private int nextRating;
    private String step;
    private String matchTime;

    public MatchRecord(int djh, String res, int change, int nextRating, String step, String matchTime) {
        this.djh = djh;
        this.res = res;
        this.change = change;
        this.nextRating = nextRating;
        this.step = step;
        this.matchTime = matchTime;
    }

    public int getDjh() {
        return djh;
    }
    
    public String getRes() {
        return res;
    }

    public int getChange() {
        return change;
    }

    public int getNextRating() {
        return nextRating;
    }
    
    public String getStep() {
        return step;
    }
    
    public String getMatchTime() {
        return matchTime;
    }

    public int getStepCount() {
        if (step == null) return 0;
        return step.trim().length() / 2;
    }
}