package gomoku.network;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 五子棋联机对战服务端
 * 负责监听端口、接入客户端长连接、线程级排队匹配、对局指令中继转发以及异常断线清理
 */
public class GameServer {
    public static final int PORT = 1145;
    private static final Queue<Player> que = new LinkedList<>();

    /**
     * 服务端启动主入口
     * @param args 启动参数
     */
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

    /**
     * 玩家处理类
     * 负责单个玩家连接的生命周期、对象流初始化等
     */
    static class Player implements Runnable {
        private Socket socket;
        private Player other;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private int userId;

        /**
         * 构造方法，初始化玩家流通道
         * @param socket 建立好的客户端物理链路
         */
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

        /**
         * 线程执行主体
         * 死循环阻塞读取客户端上报的数据包
         */
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

        /**
         * 消息处理与路由中继分发中心
         * @param msg 客户端发来的协议实体
         */
        private void messagePro(Message msg) {
            switch (msg.getType()) {
            	case Message.MATCH:
                    this.userId = msg.getSenderId();
            		match();
            		break;
            	case Message.PUSH:
            		if (other != null) {
            			other.send(msg);
            		}
            		break;
            	case Message.CHAT:
            		if (other != null) {
            			other.send(msg);
            		}
            		break;
                case Message.SUBUNDO:
                case Message.REPUNDO:
                    if (other != null) {
                        other.send(msg);
                    }
                    break;
            	default:
            		break;
            }
        }

        /**
         * 匹配排队
         * synchronized (que) 保护等待队列
         */
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
                    int bId = player1.userId;
                    int wId = player2.userId;
                    player1.send(new Message(Message.START, 1, bId, wId));
                    player2.send(new Message(Message.START, 2, bId, wId));
                    System.out.println("匹配成功！已为两名玩家创建对战房间。");
                }
            }
        }

        /**
         * 发送报文方法，使用 synchronized 修饰，防止多线程并发向同一个套接字输出流写数据导致包撕裂
         * @param msg 待发送的数据
         */
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

        /**
         * 断线处理与资源彻底回收机制
         */
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
            } catch (IOException e) {}
        }
    }
}