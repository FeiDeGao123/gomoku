package gomoku.network;

/**
 * 网络事件监听回调接口
 */
public interface IListener {
	/**
	 * 匹配等待中事件
	 */
	void playerWait();
	
	/**
	 * 匹配成功并正式开局事件
	 * @param color 服务端分配给本客户端的执棋颜色
	 */
	void gameStart(int color);

	void gameStart(int color, int blackId, int whiteId);
	
	/**
	 * 接收到对手落子事件
	 * @param x 对手落子的行坐标
	 * @param y 对手落子的列坐标
	 */
	void otherPush(int x, int y);
	
	/**
	 * 对手异常退出或逃跑断线事件
	 */
	void otherQuit();
	
	/**
	 * 接收到对手发来的即时聊天文本事件
	 * @param msg 消息字符串
	 */
	void receiveChat(String msg);
	
	/**
	 * 收到对手向己方发起的悔棋申请事件
	 */
	void subUndo();
	
	/**
	 * 收到对手针对己方悔棋申请的答复结果事件
	 * @param agree 若对手同意悔棋为 true，若对手拒绝为 false
	 */
	void repUndo(boolean agree);
}