package gomoku.network;

import java.io.*;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int MATCH = 1;
    public static final int WAIT = 2;
    public static final int START = 3;
    public static final int PUSH = 4;
    public static final int QUIT = 5;
    private int type;
    private int x;
    private int y;
    private int color;
    
    public Message() {};

    public Message(int type) {
    	this.type = type;
    }
    
    public Message(int type, int color) {
        this.type = type;
        this.color = color;
    }
    
    public Message(int type, int x, int y) {
    	this.type = type;
    	this.x = x;
    	this.y = y;
    }
    
    public int getType() {
    	return type;
    }
    
    public int getX() {
    	return x;
    }
    
    public int getY() {
    	return y;
    }
    
    public int getColor() {
    	return color;
    }
    
    public void setType(int type) {
    	this.type = type;
    }
    
    public void setX(int x) {
    	this.x = x;
    }
    
    public void setY(int y) {
    	this.y = y;
    }
    
    public void setColor(int color) {
    	this.color = color;
    }
}