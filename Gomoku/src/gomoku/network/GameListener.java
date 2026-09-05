package gomoku.network;

public interface GameListener {
	void playerWait();
	void gameStart(int color);
	void otherPush(int x, int y);
	void otherQuit();
}
