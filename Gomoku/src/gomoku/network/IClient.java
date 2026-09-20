package gomoku.network;

/**
 * 客户端网络通信服务接口
 */
public interface IClient {
	/**
	 * 注册网络事件监听器
	 * 当底层网络线程接收到底层服务端的消息时，将通过此回调对象通知 UI 界面刷新
	 * @param gl 实现了 IListener 接口的回调监听对象
	 */
	void setObj(IListener gl);
	
	/**
	 * 发起 TCP Socket 连接，并在连接建立成功后自动发送排队匹配请求
	 * @param host 目标服务器公网 IP 地址或域名
	 * @param port 目标服务器监听端口
	 */
	void sendConnect(String host, int port);
	
	/**
	 * 向服务端发送落子坐标信令，服务端接收后将透明中继转发给当前对弈的对手
	 * @param x 落子行坐标
	 * @param y 落子列坐标
	 */
	void sendPush(int x, int y);
	
	/**
	 * 向服务端发送聊天文本
	 * @param msg 消息字符串
	 */
	void sendChat(String msg);
	
	/**
	 * 向对手发起悔棋申请，发送包，请求对手同意己方回退落子
	 */
	void sendSubUndo();
	
	/**
	 * 答复对手的悔棋申请
	 * @param agree 若同意对方悔棋传 true，若拒绝传 false
	 */
	void sendRepUndo(boolean agree);
	
	/**
	 * 主动关闭套接字，释放流资源并断开连接
	 */
	void disconnect();
}