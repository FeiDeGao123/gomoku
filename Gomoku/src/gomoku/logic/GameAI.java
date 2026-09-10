package gomoku.logic;

public class GameAI {
	public int[] findMove(GameCheck gc, int aiColor) {
		int humanColor = aiColor ^ 3;
		int[][] grid = gc.getGrid();
		if (grid[7][7] == 0) {
			return new int[]{7, 7};
		}
		for (int len = 5; len >= 2; len--) {
		    for (int i = 0; i < GameCheck.SIZE; i++) {
		        for (int j = 0; j < GameCheck.SIZE; j++) {
		            if (grid[i][j] == 0 && gc.countMax(i, j, aiColor) >= len) {
		                return new int[]{i, j};
		            }
		        }
		    }
		    for (int i = 0; i < GameCheck.SIZE; i++) {
		        for (int j = 0; j < GameCheck.SIZE; j++) {
		            if (grid[i][j] == 0 && gc.countMax(i, j, humanColor) >= len) {
		                return new int[]{i, j};
		            }
		        }
		    }
		}
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] == 0 && (gc.countMax(i, j, humanColor) > 1 || gc.countMax(i, j, aiColor) > 1)) {
					return new int[]{i, j};
				}
			}
		}
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] == 0) return new int[]{i, j};
			}
		}
		return null;
	}
}