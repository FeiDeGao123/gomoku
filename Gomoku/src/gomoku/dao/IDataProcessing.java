package gomoku.dao;

import java.util.List;

/**
 * 数据持久化业务抽象接口
 */
public interface IDataProcessing {
	/**
     * 用户登录
     * @param name 用户名
     * @param password 登录密码
     * @return 成功返回 User 实体对象，账号或密码不匹配返回 null
     */
    User login(String name, String password);
    
    /**
     * 玩家注册
     * @param name 注册用户名
     * @param password 密码
     * @return 注册成功返回 true，若用户名已存在触发唯一性约束异常则返回 false
     */
    boolean register(String name, String password);
    
    /**
     * 根据主键 ID 检索玩家当前最新信息
     * @param userId 玩家唯一标识 ID
     * @return 包含最新积分与战绩的 User 实体
     */
    User getUserById(int userId);
    
    /**
     * 对局结算
     * 加 S 锁校验读取玩家当前底分
     * 获取行级 X 锁更新 player 表胜负场与天梯分
     * 写入 game_match 表保存十六进制棋谱，并回传自增对局 ID
     * 写入 rating_log 表保存积分变动审计流水
     * 异常出现，执行 rollback 回滚
     * @param userId 结算玩家的主键 ID
     * @param isWin 本局是否获胜
     * @param hexSteps 棋谱坐标序列的十六进制编码文本
     */
    void settleMatch(int userId, boolean isWin, String hexSteps);
    
    /**
     * 查询指定玩家的全部历史对局记录
     * @param userId 玩家 ID
     * @return 包含对局结果、积分变动、复盘棋谱在内的对局记录集合
     */
    List<MatchRecord> getHistoryByUserId(int userId);
    
    /**
     * 获取积分天梯榜单
     * 优先按积分降序，积分相同时按胜场数降序
     * @param limit 榜单截取名额数量
     * @return 排名靠前的玩家实体列表
     */
    List<User> getTopRankings(int limit);
}