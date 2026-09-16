package gomoku.network;

import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    public static final int PORT = 1145;
    private static final Queue<Player> que = new LinkedList<>();

    public static void main(String[] args) {
        System.out.println("服务器启动成功，端口: " + PORT);
        try (ServerSocket ss = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = ss.accept();
                System.out.println("新玩家连接: " + socket.getRemoteSocketAddress());
                Player player = new Player(socket);
                Thread thread = new Thread(player);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Player implements Runnable {
        private Socket socket;
        private Player other;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public Player(Socket socket) {
            this.socket = socket;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                	Message msg = (Message)ois.readObject();
                	messagePro(msg);
                }
            } catch (Exception e) {
                System.out.println("玩家连接断开: " + socket.getRemoteSocketAddress());
            } finally {
                handleDisconnect();
            }
        }

        private void messagePro(Message msg) {
            switch (msg.getType()) {
            	case Message.MATCH:
            		match();
            		break;
            	case Message.PUSH:
            		if (other != null) {
            			other.send(msg);
            		}
            		break;
            	default:
            		break;
            }
        }

        private void match() {
            synchronized (que) {
                if (que.isEmpty()) {
                    que.offer(this);
                    send(new Message(Message.WAIT));
                    System.out.println("玩家加入排队，当前等待人数: " + que.size());
                } else {
                	Player player1 = que.poll();
                	Player player2 = this;
                	player1.other = player2;
                	player2.other = player1;
                    player1.send(new Message(Message.START, 1));
                    player2.send(new Message(Message.START, 2));
                    System.out.println("匹配成功！已为两名玩家创建对战房间。");
                }
            }
        }

        public synchronized void send(Message msg) {
            try {
                if (oos != null) {
                    oos.writeObject(msg);
                    oos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleDisconnect() {
            synchronized (que) {
            	que.remove(this);
            }
            if (other != null) {
            	other.send(new Message(Message.QUIT));
            	other.other = null;
            }
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}