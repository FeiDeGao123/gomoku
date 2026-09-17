package gomoku.network;

public interface IListener {
	void playerWait();
	void gameStart(int color);
	void otherPush(int x, int y);
	void otherQuit();
	void receiveChat(String msg);
	void subUndo();
	void repUndo(boolean agree);
}