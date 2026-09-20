package gomoku.network;

import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * 客户端网络通信类
 */
public class SocketClient implements IClient {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private IListener gl;
	private volatile boolean isRunning;
	
	/**
	 * 注册事件监听器
	 * @param gl 实现了 IListener 接口的回调对象
	 */
	public void setObj(IListener gl) {
		this.gl = gl;
	}
	
	/**
	 * 发起与服务端的 Socket 连接并请求匹配
	 * @param host 服务器 IP 地址
	 * @param port 服务器端口号
	 */
	public void sendConnect(String host, int port) {
		Thread connectThread = new Thread(new Runnable() {
            public void run() {
                try {
                    socket = new Socket(host, port);
                    socket.setTcpNoDelay(true);
                    socket.setKeepAlive(true);

                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.flush();
                    ois = new ObjectInputStream(socket.getInputStream());
                    isRunning = true;
                    startListening();
                    send(new Message(Message.MATCH));
                } catch (IOException e) {
                    dispatchError("连接服务器失败");
                }
            }
        });
		connectThread.start();
	}
	
	/**
	 * 启动后台长连接消息监听子线程
	 * 死循环阻塞读取服务端下发的消息对象，在异常断开时提示
	 */
	private void startListening() {
		Thread listenThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (isRunning) {
					    Message msg = (Message) ois.readObject();
					    messagePro(msg);
					}
				} catch (Exception e) {
					if (isRunning) {
						dispatchError("与服务器连接中断！");
					}
				}
			}
		});
		listenThread.start();
	}

	/**
	 * 消息协议路由分发
	 * @param msg 接收到的服务端协议对象
	 */
	private void messagePro(Message msg) {
		switch (msg.getType()) {
			case Message.WAIT:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.playerWait());
				break;
			case Message.START:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.gameStart(msg.getColor()));
				break;
			case Message.PUSH:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.otherPush(msg.getX(), msg.getY()));
				break;
			case Message.QUIT:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.otherQuit());
				break;
			case Message.CHAT:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.receiveChat(msg.getChatMsg()));
				break;
			case Message.SUBUNDO:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.subUndo()); 
				break;
			case Message.REPUNDO:
				if (gl != null) SwingUtilities.invokeLater(() -> gl.repUndo(msg.isAgree()));
				break;
			default:
				break;
		}
	}

	/**
	 * 发送落子坐标
	 * @param x 落子行坐标
	 * @param y 落子列坐标
	 */
	public void sendPush(int x, int y) {
		send(new Message(Message.PUSH, x, y));
	}

	/**
	 * 发送聊天内容
	 * @param content 聊天文字
	 */
	public void sendChat(String content) {
		send(new Message(Message.CHAT, content));
	}

	/**
	 * 发起悔棋申请
	 */
	public void sendSubUndo() {
		send(new Message(Message.SUBUNDO));
	}

	/**
	 * 答复悔棋申请
	 * @param agree 是否同意
	 */
	public void sendRepUndo(boolean agree) {
		send(new Message(Message.REPUNDO, agree));
	}

	/**
	 * 报文发送
	 * @param msg 待发送报文
	 */
	private synchronized void send(Message msg) {
		try {
			if (oos != null) {
				oos.writeObject(msg);
				oos.flush();
				oos.reset();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 主动安全断开连接
	 */
	public void disconnect() {
		isRunning = false;
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException ignored) {}
	}

	private void dispatchError(String message) {
		SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, message));
	}
}