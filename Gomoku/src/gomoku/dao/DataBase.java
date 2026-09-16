package gomoku.dao;

import java.sql.*;

public class DataBase {
    private static final String USER = "gomoku";
    private static final String PASS = "GbTjKpWwaQP6JE5J";
    private static final String DB = "jdbc:mysql://124.223.22.121:3306/gomoku?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8";

    static {
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Class.forName("com.mysql.jdbc.Driver");
            }
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("create table if not exists `player` ("
                    + "id int auto_increment primary key,"
                    + "username varchar(50) not null unique,"
                    + "password varchar(64) not null,"
                    + "rating int default 1000,"
                    + "win_cnt int default 0,"
                    + "lose_cnt int default 0,"
                    + "created_at timestamp default current_timestamp"
                    + ") engine=innodb default charset=utf8mb4;");
            stmt.executeUpdate("create table if not exists `game_match` ("
                    + "match_id int auto_increment primary key,"
                    + "black_id int not null,"
                    + "white_id int not null,"
                    + "winner_id int null,"
                    + "total_steps int not null,"
                    + "hex_steps mediumtext not null,"
                    + "finish_reason varchar(20) default 'normal',"
                    + "played_at timestamp default current_timestamp,"
                    + "foreign key (black_id) references `player`(id),"
                    + "foreign key (white_id) references `player`(id)"
                    + ") engine=innodb default charset=utf8mb4;");
            stmt.executeUpdate("create table if not exists `rating_log` ("
                    + "log_id int auto_increment primary key,"
                    + "player_id int not null,"
                    + "match_id int not null,"
                    + "change_amount int not null,"
                    + "after_rating int not null,"
                    + "reason varchar(20) not null,"
                    + "created_at timestamp default current_timestamp,"
                    + "foreign key (player_id) references `player`(id),"
                    + "foreign key (match_id) references `game_match`(match_id)"
                    + ") engine=innodb default charset=utf8mb4;");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB, USER, PASS);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ignored) {}
    }
}