package gomoku.dao;

/**
 * 玩家信息类
 * 负责在内存中封装玩家核心数据，包括用户主键、用户名、天梯积分以及历史胜负场次
 */
public class User {
	private int id;
	private String name;
	private int score;
	private int winCnt;
	private int loseCnt;
	
	/**
	 * 构造方法，初始化完整的玩家实体对象
	 * @param id 玩家 ID
	 * @param name 玩家用户名
	 * @param score 积分
	 * @param winCnt 累计胜场
	 * @param loseCnt 累计负场
	 */
	public User(int id, String name, int score, int winCnt, int loseCnt) {
		this.id = id;
		this.name = name;
		this.score = score;
		this.winCnt = winCnt;
		this.loseCnt = loseCnt;
	}
	
	/**
	 * 获取玩家唯一标识 ID
	 * @return 玩家 ID
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * 获取玩家用户名
	 * @return 用户名文本
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 获取玩家当前积分
	 * @return 积分
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * 获取玩家累计获胜局数
	 * @return 胜场数
	 */
	public int getWinCnt() {
		return winCnt;
	}
	
	/**
	 * 获取玩家累计失败局数
	 * @return 负场数
	 */
	public int getLoseCnt() {
		return loseCnt;
	}
	
	/**
	 * 更新玩家用户名
	 * @param name 新的用户名文本
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 更新玩家积分
	 * @param score 结算后的新积分
	 */
	public void setScore(int score) {
		this.score = score;
	}
	
	/**
	 * 更新玩家累计胜场数
	 * @param winCnt 更新后的胜场数
	 */
	public void setWinCnt(int winCnt) {
		this.winCnt = winCnt;
	}
	
	/**
	 * 更新玩家累计负场数
	 * @param loseCnt 更新后的负场数
	 */
	public void setLoseCnt(int loseCnt) {
		this.loseCnt = loseCnt;
	}
}
