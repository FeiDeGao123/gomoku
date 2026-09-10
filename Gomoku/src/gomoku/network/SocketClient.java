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
				if (gl != null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							gl.playerWait();
						}
					});
				}
				break;
			case Message.START:
				if (gl != null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							gl.gameStart(msg.getColor());
						}
					});
				}
				break;
			case Message.PUSH:
				if (gl != null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							gl.otherPush(msg.getX(), msg.getY());
						}
					});
				}
				break;
			case Message.QUIT:
				if (gl != null) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							gl.otherQuit();
						}
					});
				}
				break;
			default:
				break;
		}
	}

	public void sendPush(int x, int y) {
		send(new Message(Message.PUSH, x, y));
	}

	private synchronized void send(Message msg) {
		try {
			if (oos != null) {
				oos.writeObject(msg);
				oos.flush();
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
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, message);
			}
		});
	}
}
