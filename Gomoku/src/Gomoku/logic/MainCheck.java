package Gomoku.logic;

public class MainCheck {
	public static final int SIZE = 15;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	public static final int dx[] = {-1, -1, 0, 1};
	public static final int dy[] = {0, 1, 1, 1};
	private int grid[][];
	
	public MainCheck() {
		grid = new int[SIZE][SIZE];
	}
	
	public boolean checkPush(int x, int y, int color) {
		if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || grid[x][y] != 0) return false;
		grid[x][y] = color;
		return true;
	}
	
	public boolean checkBound(int x, int y) {
		if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return false;
		return true;
	}
	
	public boolean checkEnd(int x, int y, int color) {
        for (int d = 0; d < 4; d++) {
        	int cnt1 = 1, cnt2 = 1;
        	while (checkBound(x + cnt1 * dx[d], y + cnt1 * dy[d]) && grid[x + cnt1 * dx[d]][y + cnt1 * dy[d]] == color) {
        		cnt1++;
        	}
        	while (checkBound(x - cnt2 * dx[d], y - cnt2 * dy[d]) && grid[x - cnt2 * dx[d]][y - cnt2 * dy[d]] == color) {
        		cnt2++;
        	}
        	int cnt = cnt1 + cnt2 - 1;
        	if (cnt >= 5) return true;
        }
        return false;
	}
	
	public int[][] getGrid() {
		return grid;
	}
}
