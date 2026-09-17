package gomoku.network;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class SocketClient implements IClient {
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private IListener gl;
	private volatile boolean isRunning;
	
	public void setObj(IListener gl) {
		this.gl = gl;
	}
	
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

	public void sendPush(int x, int y) {
		send(new Message(Message.PUSH, x, y));
	}

	public void sendChat(String content) {
		send(new Message(Message.CHAT, content));
	}

	public void sendSubUndo() {
		send(new Message(Message.SUBUNDO));
	}

	public void sendRepUndo(boolean agree) {
		send(new Message(Message.REPUNDO, agree));
	}

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