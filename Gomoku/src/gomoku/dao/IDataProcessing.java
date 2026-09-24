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
     * 单人/人机/单视角对局结算 (保持对原有代码兼容)
     * @param userId   结算玩家的主键 ID
     * @param isWin    本局是否获胜
     * @param hexSteps 棋谱坐标序列的十六进制编码文本
     */
    void settleMatch(int userId, boolean isWin, String hexSteps);

    /**
     * 双方对战对局结算
     * @param blackId  黑方选手 ID
     * @param whiteId  白方选手 ID
     * @param winnerId 胜者 ID
     * @param hexSteps 棋谱坐标序列的十六进制编码文本
     */
    void settleMatch(int blackId, int whiteId, int winnerId, String hexSteps);
    
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