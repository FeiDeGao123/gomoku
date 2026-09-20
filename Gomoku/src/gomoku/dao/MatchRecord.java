package gomoku.dao;

/**
 * 对局记录类
 * 负责封装单场比赛的对局编号、输赢战果、天梯分变动、棋谱编码串及对局时间
 */
public class MatchRecord {
    private int djh;
    private String res;
    private int change;
    private int nextRating;
    private String step;
    private String matchTime;

    /**
     * 初始化完整的对局记录实体
     * @param djh 对局编号
     * @param res 结果
     * @param change 积分变动量
     * @param nextRating 赛后最新积分
     * @param step 十六进制棋谱字符串
     * @param matchTime 对局完成时间
     */
    public MatchRecord(int djh, String res, int change, int nextRating, String step, String matchTime) {
        this.djh = djh;
        this.res = res;
        this.change = change;
        this.nextRating = nextRating;
        this.step = step;
        this.matchTime = matchTime;
    }

    /**
     * 获取对局编号
     * @return 对局唯一标识 ID
     */
    public int getDjh() {
        return djh;
    }
    
    /**
     * 获取比赛结果
     * @return "胜利" 或 "失败" 文本
     */
    public String getRes() {
        return res;
    }

    /**
     * 获取积分变动
     * @return 增加或扣除的分数值
     */
    public int getChange() {
        return change;
    }

    /**
     * 获取结算后的最新积分
     * @return 赛后积分值
     */
    public int getNextRating() {
        return nextRating;
    }
    
    /**
     * 获取十六进制棋谱字符串
     * @return 棋谱字符串
     */
    public String getStep() {
        return step;
    }
    
    /**
     * 获取对局发生的时间文本
     * @return 对局时间字符串
     */
    public String getMatchTime() {
        return matchTime;
    }

    /**
     * 计算并获取该场对局双方落子的实际总步数
     * 棋谱中每手棋占用 2 个十六进制字符，因此总步数为字符串有效长度除以 2
     * @return 该局总落子步数
     */
    public int getStepCount() {
        if (step == null) return 0;
        return step.trim().length() / 2;
    }
}