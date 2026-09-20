package gomoku.dao;

import java.sql.*;

/**
 * 数据库连接与工具类
 * 加载 MySQL JDBC 驱动、维护连接参数、程序启动时建表、释放连接资源
 */
public class DataBase {
    private static final String USER = "gomoku";
    private static final String PASS = "GbTjKpWwaQP6JE5J";
    private static final String DB = "jdbc:mysql://124.223.22.121:3306/gomoku?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8";
    
    /**
     * 在类加载时自动且仅执行一次，负责驱动加载与数据库表结构初始化
     */
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
    
    /**
     * 数据库表结构自动化初始化方法
     * 若数据库中尚未存在表，则自动执行 DDL 语句创建表
     */
    private static void init() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
        	// 玩家表：ID、用户名、密码、积分、胜负场次、注册时间
            stmt.executeUpdate("create table if not exists `player` ("
                    + "id int auto_increment primary key,"
                    + "username varchar(50) not null unique,"
                    + "password varchar(64) not null,"
                    + "rating int default 1000,"
                    + "win_cnt int default 0,"
                    + "lose_cnt int default 0,"
                    + "created_at timestamp default current_timestamp"
                    + ") engine=innodb default charset=utf8mb4;");
            // 对局记录表：双方ID、胜者ID、总步数、十六进制存棋谱、结束原因、时间
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
            // 积分变动表：追踪玩家积分分变动
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

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取数据库物理连接
     * @return Connection 处于打开状态的 JDBC 物理连接对象
     * @throws SQLException 若连接超时、认证失败或网络中断则抛出异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB, USER, PASS);
    }
    
    /**
     * 释放数据库资源的方法
     * @param conn 待关闭的数据库物理连接
     * @param stmt 待关闭的 SQL 语句执行对象
     * @param rs   待关闭的查询结果集对象
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } 
        catch (SQLException e) {}
    }
}