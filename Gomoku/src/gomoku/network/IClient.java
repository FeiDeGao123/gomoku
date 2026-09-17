package gomoku.network;

public interface IClient {
	void setObj(IListener gl);
	void sendConnect(String host, int port);
	void sendPush(int x, int y);
	void sendChat(String msg);
	void sendSubUndo();
	void sendRepUndo(boolean agree);
	void disconnect();
}