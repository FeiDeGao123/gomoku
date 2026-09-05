package gomoku.network;

public interface GameClient {
	void setObj(GameListener gl);
	void sendConnect(String host, int port);
	void sendPush(int x, int y);
	void disconnect();
}
