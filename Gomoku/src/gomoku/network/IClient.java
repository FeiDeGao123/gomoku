package gomoku.network;

public interface IClient {
	void setObj(IListener gl);
	void sendConnect(String host, int port);
	void sendPush(int x, int y);
	void disconnect();
}
