package gomoku.network;

import java.io.*;

/**
 * 网络通信协议报文类
 */
public class Message implements Serializable {
	// 版本标识符，确保对象序列化与反序列化时的类结构一致性
	private static final long serialVersionUID = 1L;
	public static final int MATCH = 1;
    public static final int WAIT = 2;
    public static final int START = 3;
    public static final int PUSH = 4;
    public static final int QUIT = 5;
    public static final int CHAT = 6;
    public static final int SUBUNDO = 7;
    public static final int REPUNDO = 8;
    private int type;
    private int x;
    private int y;
    private int color;
    private String chatMsg;
    private boolean agree;
    
    /**
     * 无参构造方法
     */
    public Message() {}

    /**
     * 构造控制状态类报文
     * @param type 协议类型
     */
    public Message(int type) {
    	this.type = type;
    }
    
    /**
     * 构造开局执色报文
     * @param type 协议类型
     * @param color 分配的棋子颜色
     */
    public Message(int type, int color) {
        this.type = type;
        this.color = color;
    }
    
    /**
     * 构造落子坐标报文
     * @param type 协议类型
     * @param x 落子行坐标
     * @param y 落子列坐标
     */
    public Message(int type, int x, int y) {
    	this.type = type;
    	this.x = x;
    	this.y = y;
    }
    
    /**
     * 构造聊天文本报文
     * @param type 协议类型
     * @param chatMsg 聊天文本
     */
    public Message(int type, String chatMsg) {
    	this.type = type;
    	this.chatMsg = chatMsg;
    }

    /**
     * 构造悔棋协商报文
     * @param type 协议类型
     * @param agree 是否同意悔棋
     */
    public Message(int type, boolean agree) {
        this.type = type;
        this.agree = agree;
    }
    
    /**
     * 获取当前报文的类型
     * @return 类型
     */
    public int getType() {
    	return type;
    }
    
    /**
     * 获取落子行坐标
     * @return 行坐标
     */
    public int getX() {
    	return x;
    }
    
    /**
     * 获取落子列坐标
     * @return 列坐标
     */
    public int getY() {
    	return y;
    }
    
    /**
     * 获取执子颜色
     * @return 1-黑棋，2-白棋
     */
    public int getColor() {
    	return color;
    }
    
    /**
     * 获取聊天文本内容
     * @return 文本字符串
     */
    public String getChatMsg() {
    	return chatMsg;
    }
    
    /**
     * 获取悔棋答复
     * @return true同意，false拒绝
     */
    public boolean isAgree() {
    	return agree;
    }
    
    /**
     * 设置报文类型
     * @param type 报文类型
     */
    public void setType(int type) {
    	this.type = type;
    }
    
    /**
     * 设置落子行坐标
     * @param x 行坐标
     */
    public void setX(int x) {
    	this.x = x;
    }
    
    /**
     * 设置落子列坐标
     * @param y 列坐标
     */
    public void setY(int y) {
    	this.y = y;
    }
    
    /**
     * 设置执子颜色
     * @param color 颜色
     */
    public void setColor(int color) {
    	this.color = color;
    }
    
    /**
     * 设置聊天文本
     * @param chatMsg 聊天内容
     */
    public void setChatMsg(String chatMsg) {
    	this.chatMsg = chatMsg;
    }
    
    /**
     * 设置悔棋答复结果
     * @param agree 是否同意
     */
    public void setAgree(boolean agree) {
    	this.agree = agree;
    }
}