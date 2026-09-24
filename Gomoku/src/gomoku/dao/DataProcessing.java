package gomoku.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务处理类
 * 用户身份认证、战绩持久化结算、历史棋谱提取、排行榜处理
 * 遵循二级封锁协议与两段锁协议
 */
public class DataProcessing implements IDataProcessing {
	/**
     * 用户登录
     * @param name 用户名
     * @param password 登录密码
     * @return 成功返回 User 实体对象，账号或密码不匹配返回 null
     */
    public User login(String name, String password) {
        String sql = "select * from `player` where username = ? and password = ? lock in share mode";
        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getInt("rating"),
                            rs.getInt("win_cnt"),
                            rs.getInt("lose_cnt")
                        );
                        conn.commit();
                        return user;
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
        return null;
    }
    
    /**
     * 玩家注册
     * @param name 注册用户名
     * @param password 密码
     * @return 注册成功返回 true，若用户名已存在触发唯一性约束异常则返回 false
     */
    public boolean register(String name, String password) {
        String sql = "insert into `player` (username, password) values (?, ?)";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * 根据主键 ID 检索玩家当前最新信息
     * @param userId 玩家唯一标识 ID
     * @return 包含最新积分与战绩的 User 实体
     */
    public User getUserById(int userId) {
        String sql = "select * from `player` where id = ? lock in share mode";
        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getInt("rating"),
                            rs.getInt("win_cnt"),
                            rs.getInt("lose_cnt")
                        );
                        conn.commit();
                        return user;
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
        return null;
    }

    /**
     * 单人对局结算
     * @param userId 结算玩家的主键 ID
     * @param isWin 本局是否获胜
     * @param hexSteps 棋谱坐标序列的十六进制编码文本
     */
    public void settleMatch(int userId, boolean isWin, String hexSteps) {
        int delta = isWin ? 10 : -5;
        String reason = isWin ? "WIN" : "LOSS";

        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
            int currentScore = 1000;
            String querySql = "select rating from `player` where id = ? lock in share mode";
            try (PreparedStatement ps0 = conn.prepareStatement(querySql)) {
                ps0.setInt(1, userId);
                try (ResultSet rs0 = ps0.executeQuery()) {
                    if (rs0.next()) {
                        currentScore = rs0.getInt("rating");
                    }
                }
            }
            int afterRating = isWin ? (currentScore + 10) : Math.max(0, currentScore - 5);
            String updatePlayerSql = isWin 
                ? "update `player` set rating = rating + 10, win_cnt = win_cnt + 1 where id = ?"
                : "update `player` set rating = greatest(0, rating - 5), lose_cnt = lose_cnt + 1 where id = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(updatePlayerSql)) {
                ps1.setInt(1, userId);
                ps1.executeUpdate();
            }
            int matchId = 0;
            String insertMatchSql = "insert into `game_match` (black_id, white_id, winner_id, total_steps, hex_steps, finish_reason) values (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(insertMatchSql, Statement.RETURN_GENERATED_KEYS)) {
                ps2.setInt(1, userId); 
                ps2.setInt(2, userId);
                if (isWin) {
                    ps2.setInt(3, userId);
                } else {
                    ps2.setNull(3, Types.INTEGER);
                }
                ps2.setInt(4, hexSteps == null ? 0 : hexSteps.length() / 2);
                ps2.setString(5, hexSteps == null ? "" : hexSteps);
                ps2.setString(6, "NORMAL");
                ps2.executeUpdate();
                try (ResultSet rsKey = ps2.getGeneratedKeys()) {
                    if (rsKey.next()) matchId = rsKey.getInt(1);
                }
            }
            if (matchId > 0) {
                String insertLogSql = "insert into `rating_log` (player_id, match_id, change_amount, after_rating, reason) values (?, ?, ?, ?, ?)";
                try (PreparedStatement ps3 = conn.prepareStatement(insertLogSql)) {
                    ps3.setInt(1, userId);
                    ps3.setInt(2, matchId);
                    ps3.setInt(3, delta);
                    ps3.setInt(4, afterRating);
                    ps3.setString(5, reason);
                    ps3.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * 双方对战标准对局结算
     * 一场比赛仅在 game_match 插入一条对局记录，同时为双方生成 rating_log
     * @param blackId 黑方选手 ID
     * @param whiteId 白方选手 ID
     * @param winnerId 胜者 ID (平局为 0 或负数)
     * @param hexSteps 棋谱十六进制编码文本
     */
    public void settleMatch(int blackId, int whiteId, int winnerId, String hexSteps) {
        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
            int loserId = (winnerId == blackId) ? whiteId : blackId;
            int winAfterRating = 1010;
            if (winnerId > 0) {
                String queryWinSql = "select rating from `player` where id = ? lock in share mode";
                try (PreparedStatement ps0 = conn.prepareStatement(queryWinSql)) {
                    ps0.setInt(1, winnerId);
                    try (ResultSet rs = ps0.executeQuery()) {
                        if (rs.next()) winAfterRating = rs.getInt("rating") + 10;
                    }
                }
                String winSql = "update `player` set rating = rating + 10, win_cnt = win_cnt + 1 where id = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(winSql)) {
                    ps1.setInt(1, winnerId);
                    ps1.executeUpdate();
                }
            }
            int loseAfterRating = 995;
            if (loserId > 0 && loserId != winnerId) {
                String queryLoseSql = "select rating from `player` where id = ? lock in share mode";
                try (PreparedStatement ps0 = conn.prepareStatement(queryLoseSql)) {
                    ps0.setInt(1, loserId);
                    try (ResultSet rs = ps0.executeQuery()) {
                        if (rs.next()) loseAfterRating = Math.max(0, rs.getInt("rating") - 5);
                    }
                }
                String loseSql = "update `player` set rating = greatest(0, rating - 5), lose_cnt = lose_cnt + 1 where id = ?";
                try (PreparedStatement psL = conn.prepareStatement(loseSql)) {
                    psL.setInt(1, loserId);
                    psL.executeUpdate();
                }
            }
            int matchId = 0;
            String insertMatchSql = "insert into `game_match` (black_id, white_id, winner_id, total_steps, hex_steps, finish_reason) values (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(insertMatchSql, Statement.RETURN_GENERATED_KEYS)) {
                ps2.setInt(1, blackId);
                ps2.setInt(2, whiteId);
                if (winnerId > 0) {
                    ps2.setInt(3, winnerId);
                } else {
                    ps2.setNull(3, Types.INTEGER);
                }
                ps2.setInt(4, hexSteps == null ? 0 : hexSteps.length() / 2);
                ps2.setString(5, hexSteps == null ? "" : hexSteps);
                ps2.setString(6, "NORMAL");
                ps2.executeUpdate();
                try (ResultSet rsKey = ps2.getGeneratedKeys()) {
                    if (rsKey.next()) matchId = rsKey.getInt(1);
                }
            }
            if (matchId > 0) {
                String insertLogSql = "insert into `rating_log` (player_id, match_id, change_amount, after_rating, reason) values (?, ?, ?, ?, ?)";
                try (PreparedStatement ps3 = conn.prepareStatement(insertLogSql)) {
                    // 胜方
                    if (winnerId > 0) {
                        ps3.setInt(1, winnerId);
                        ps3.setInt(2, matchId);
                        ps3.setInt(3, 10);
                        ps3.setInt(4, winAfterRating);
                        ps3.setString(5, "WIN");
                        ps3.executeUpdate();
                    }
                    // 负方
                    if (loserId > 0 && loserId != winnerId) {
                        ps3.setInt(1, loserId);
                        ps3.setInt(2, matchId);
                        ps3.setInt(3, -5);
                        ps3.setInt(4, loseAfterRating);
                        ps3.setString(5, "LOSS");
                        ps3.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }

    /**
     * 查询指定玩家的全部历史对局记录
     * @param userId 玩家 ID
     * @return 包含对局结果、积分变动、复盘棋谱在内的对局记录集合
     */
    public List<MatchRecord> getHistoryByUserId(int userId) {
        List<MatchRecord> list = new ArrayList<>();
        String sql = "select m.match_id, m.winner_id, m.hex_steps, m.played_at, "
                   + "       l.change_amount, l.after_rating "
                   + "from `game_match` m "
                   + "inner join `rating_log` l on m.match_id = l.match_id and l.player_id = ? "
                   + "where m.black_id = ? or m.white_id = ? "
                   + "order by m.match_id desc lock in share mode";

        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, userId);
                ps.setInt(3, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int winnerId = rs.getInt("winner_id");
                        boolean isWin = (winnerId == userId);
                        int change = rs.getInt("change_amount");
                        int nextRating = rs.getInt("after_rating");

                        list.add(new MatchRecord(
                            rs.getInt("match_id"),
                            isWin ? "胜利" : "失败",
                            change,
                            nextRating,
                            rs.getString("hex_steps"),
                            rs.getString("played_at")
                        ));
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
        return list;
    }
    
    /**
     * 获取积分天梯榜单
     * 优先按积分降序，积分相同时按胜场数降序
     * @param limit 榜单截取名额数量
     * @return 排名靠前的玩家实体列表
     */
    public List<User> getTopRankings(int limit) {
        List<User> list = new ArrayList<>();
        String sql = "select * from `player` order by rating desc, win_cnt desc limit ? lock in share mode";
        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getInt("rating"),
                            rs.getInt("win_cnt"),
                            rs.getInt("lose_cnt")
                        ));
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
        return list;
    }
}