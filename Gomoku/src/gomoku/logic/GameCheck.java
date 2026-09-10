package gomoku.logic;

public class GameCheck {
	public static final int SIZE = 15;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	private static final int dx[] = {-1, -1, 0, 1};
	private static final int dy[] = {0, 1, 1, 1};
	private int grid[][];
	
	public GameCheck() {
		grid = new int[SIZE][SIZE];
	}
	
	public boolean checkBound(int x, int y) {
		return !(x < 0 || x >= SIZE || y < 0 || y >= SIZE);
	}
	
	public boolean checkPush(int x, int y, int color) {
		if (!checkBound(x, y) || grid[x][y] != 0) return false;
		grid[x][y] = color;
		return true;
	}
	
	public int countMax(int x, int y, int color) {
		int mx = 1;
		for (int d = 0; d < 4; d++) {
        	int cnt1 = 1, cnt2 = 1;
        	while (checkBound(x + cnt1 * dx[d], y + cnt1 * dy[d]) && grid[x + cnt1 * dx[d]][y + cnt1 * dy[d]] == color) {
        		cnt1++;
        	}
        	while (checkBound(x - cnt2 * dx[d], y - cnt2 * dy[d]) && grid[x - cnt2 * dx[d]][y - cnt2 * dy[d]] == color) {
        		cnt2++;
        	}
        	int cnt = cnt1 + cnt2 - 1;
        	mx = Math.max(mx, cnt);
        }
		return mx;
	}
	
	public boolean checkEnd(int x, int y, int color) {
        return countMax(x, y, color) >= 5;
	}
	
	public void reset() {
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            grid[i][j] = 0;
	        }
	    }
	}
	
	public int[][] getGrid() {
		return grid;
	}
}
