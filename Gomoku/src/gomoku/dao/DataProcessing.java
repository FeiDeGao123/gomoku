package gomoku.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataProcessing {
    public User login(String name, String password) {
        String sql = "select * from `player` where username = ? and password = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("rating"),
                        rs.getInt("win_cnt"),
                        rs.getInt("lose_cnt")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public User getUserById(int userId) {
        String sql = "select * from `player` where id = ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("rating"),
                        rs.getInt("win_cnt"),
                        rs.getInt("lose_cnt")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void settleMatch(int userId, boolean isWin, String hexSteps) {
        int delta = isWin ? 10 : -5;
        String reason = isWin ? "WIN" : "LOSS";
        User currentUser = getUserById(userId);
        int currentScore = (currentUser != null) ? currentUser.getScore() : 1000;
        int afterRating = isWin ? (currentScore + 10) : Math.max(0, currentScore - 5);

        Connection conn = null;
        try {
            conn = DataBase.getConnection();
            conn.setAutoCommit(false);
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
                if (isWin) ps2.setInt(3, userId);
                else ps2.setNull(3, Types.INTEGER);
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

    public List<MatchRecord> getHistoryByUserId(int userId) {
        List<MatchRecord> list = new ArrayList<>();
        String sql = "select m.match_id, m.winner_id, m.hex_steps, m.played_at, "
                   + "       l.change_amount, l.after_rating "
                   + "from `game_match` m "
                   + "inner join `rating_log` l on m.match_id = l.match_id and l.player_id = ? "
                   + "where m.black_id = ? or m.white_id = ? "
                   + "order by m.match_id desc";

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
                        isWin ? "Ê¤Àû" : "Ê§°Ü",
                        change,
                        nextRating,
                        rs.getString("hex_steps"),
                        rs.getString("played_at")
                    ));
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<User> getTopRankings(int limit) {
        List<User> list = new ArrayList<>();
        String sql = "select * from `player` order by rating desc, win_cnt desc limit ?";
        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}