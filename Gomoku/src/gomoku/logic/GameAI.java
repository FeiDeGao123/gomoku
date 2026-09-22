package gomoku.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 五子棋人机对战类
 * 简单难度：贪心算法，人类思维
 * 中等难度：评估表的启发式评估
 * 困难难度：极小化极大博弈，Alpha-Beta 剪枝，迭代加深搜索
 */
public class GameAI implements IGameAI {
	public static final int EASY = 1;
	public static final int MID = 2;
	public static final int HARD = 3;
	private static final int FIVE = 1000000;
	private static final int GOOD_FOUR = 100000;
	private static final int BAD_FOUR = 10000;
	private static final int GOOD_THREE = 5000;
	private static final int BAD_THREE = 500;
	private static final int GOOD_TWO = 100;
	private static final int BAD_TWO = 10;
	private static final int[][] DIR = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
	private static final int[][] POS_VALUE = {
		{0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},
		{0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0},
		{0,  1,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  1,  0},
		{0,  1,  2,  3,  3,  3,  3,  3,  3,  3,  3,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  4,  4,  4,  4,  4,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  5,  5,  5,  5,  5,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  5,  6,  6,  6,  5,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  5,  6,  8,  6,  5,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  5,  6,  6,  6,  5,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  5,  5,  5,  5,  5,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  4,  4,  4,  4,  4,  4,  4,  3,  2,  1,  0},
		{0,  1,  2,  3,  3,  3,  3,  3,  3,  3,  3,  3,  2,  1,  0},
		{0,  1,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  1,  0},
		{0,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0},
		{0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}
	};
	private long searchStartTime;
	private long timeLimitMs = 3000;
	private boolean isTimeUp = false;
	private final char[] lineBuf = new char[9];

	/**
	 * 寻找最佳落子点
	 * @param gc 逻辑判断对象
	 * @param aiColor AI 执子颜色
	 * @return 最佳落子坐标 [x, y]
	 */
	public int[] findMove(IGameCheck gc, int aiColor) {
		return findMove(gc, aiColor, HARD);
	}

	/**
	 * 寻找最佳落子点
	 * @param gc 逻辑判断对象
	 * @param aiColor AI 执子颜色
	 * @param level 难度等级 (EASY / MID / HARD)
	 * @return 最佳落子坐标 [x, y]
	 */
	public int[] findMove(IGameCheck gc, int aiColor, int level) {
		if (level == HARD) {
			return findMoveHard(gc, aiColor);
		} else if (level == MID) {
			return findMoveMid(gc, aiColor);
		}
		return findMoveEasy(gc, aiColor);
	}

	/**
	 * 简单难度 AI，贪心落子
	 */
	private int[] findMoveEasy(IGameCheck gc, int aiColor) {
		int humanColor = aiColor ^ 3;
		int[][] grid = gc.getGrid();
		if (grid[7][7] == 0) return new int[]{7, 7};
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

	/**
	 * 中等难度 AI，权值评估算法
	 */
	private int[] findMoveMid(IGameCheck gc, int aiColor) {
		int humanColor = aiColor ^ 3;
		int[][] grid = gc.getGrid();
		if (grid[7][7] == 0) return new int[]{7, 7};
		int bestX = -1, bestY = -1;
		long maxScore = Long.MIN_VALUE;
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0) continue;
				if (!hasNeighbor(grid, i, j, 2)) continue;
				int atk = evaluatePointOnBoard(grid, i, j, aiColor);
				int def = evaluatePointOnBoard(grid, i, j, humanColor);
				if (atk >= FIVE) return new int[]{i, j};
				if (def >= FIVE) return new int[]{i, j};
				if (def >= GOOD_FOUR) def = (int)(GOOD_FOUR * 2.5);
				else if (def >= GOOD_THREE) def = (int)(GOOD_THREE * 1.8);
				long total = atk + (long)(def * 1.2) + POS_VALUE[i][j];
				if (total > maxScore) {
					maxScore = total;
					bestX = i;
					bestY = j;
				}
			}
		}
		if (bestX == -1) return findMoveEasy(gc, aiColor);
		return new int[]{bestX, bestY};
	}

	/**
	 * 困难难度 AI，极小化极大博弈树，Alpha-Beta 剪枝，迭代加深
	 */
	private int[] findMoveHard(IGameCheck gc, int aiColor) {
		int humanColor = aiColor ^ 3;
		int[][] grid = gc.getGrid();
		if (grid[7][7] == 0) return new int[]{7, 7};
		int stoneCount = 0;
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0) stoneCount++;
			}
		}
		if (stoneCount == 1 && grid[7][7] == humanColor) {
			return new int[]{6, 6};
		}

		// 能直接赢就下
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] == 0 && hasNeighbor(grid, i, j, 1)) {
					grid[i][j] = aiColor;
					boolean win = gc.checkEnd(i, j, aiColor);
					grid[i][j] = 0;
					if (win) {
						return new int[]{i, j};
					}
				}
			}
		}

		// 对手能直接赢，堵住对手
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] == 0 && hasNeighbor(grid, i, j, 1)) {
					grid[i][j] = humanColor;
					boolean humanWin = gc.checkEnd(i, j, humanColor);
					grid[i][j] = 0;
					if (humanWin) {
						return new int[]{i, j};
					}
				}
			}
		}
		searchStartTime = System.currentTimeMillis();
		isTimeUp = false;
		List<MoveScore> candidateMoves = generateSortedMoves(grid, aiColor, humanColor, 20);
		if (candidateMoves.isEmpty()) return findMoveMid(gc, aiColor);
		int[] bestMove = new int[]{candidateMoves.get(0).x, candidateMoves.get(0).y};
		// 迭代加深搜索
		for (int depth = 2; depth <= 10; depth += 2) {
			int currentBestX = bestMove[0];
			int currentBestY = bestMove[1];
			int bestVal = Integer.MIN_VALUE;
			int alpha = -50000000;
			int beta = 50000000;
			// 将上一轮迭代找到的最佳点排在首位，触发最大概率的 Alpha 剪枝
			for (int k = 0; k < candidateMoves.size(); k++) {
				if (candidateMoves.get(k).x == currentBestX && candidateMoves.get(k).y == currentBestY) {
					MoveScore pv = candidateMoves.remove(k);
					candidateMoves.add(0, pv);
					break;
				}
			}
			for (MoveScore m : candidateMoves) {
				grid[m.x][m.y] = aiColor;
				if (gc.checkEnd(m.x, m.y, aiColor)) {
					grid[m.x][m.y] = 0;
					return new int[]{m.x, m.y};
				}
				int val = minNode(gc, grid, depth - 1, alpha, beta, aiColor, humanColor);
				grid[m.x][m.y] = 0;

				if (isTimeUp) {
					break;
				}
				if (val > bestVal) {
					bestVal = val;
					currentBestX = m.x;
					currentBestY = m.y;
				}
				alpha = Math.max(alpha, val);
			}
			if (isTimeUp) {
				break;
			} else {
				bestMove[0] = currentBestX;
				bestMove[1] = currentBestY;
			}
		}
		return bestMove;
	}

	/**
	 * 博弈树 Max 节点
	 */
	private int maxNode(IGameCheck gc, int[][] grid, int depth, int alpha, int beta, int aiColor, int humanColor) {
		if (depth == 0 || isTimeUp) {
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		if (System.currentTimeMillis() - searchStartTime > timeLimitMs) {
			isTimeUp = true;
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		// 只看最优秀的 4~6 个点，减少状态数
		int count = (depth >= 7) ? 4 : ((depth >= 5) ? 6 : ((depth >= 3) ? 10 : 14));
		List<MoveScore> moves = generateSortedMoves(grid, aiColor, humanColor, count);
		if (moves.isEmpty()) return 0;
		int maxVal = Integer.MIN_VALUE;
		for (MoveScore m : moves) {
			grid[m.x][m.y] = aiColor;
			if (gc.checkEnd(m.x, m.y, aiColor)) {
				grid[m.x][m.y] = 0;
				return FIVE - (10 - depth) * 10000;
			}
			int val = minNode(gc, grid, depth - 1, alpha, beta, aiColor, humanColor);
			grid[m.x][m.y] = 0;
			maxVal = Math.max(maxVal, val);
			alpha = Math.max(alpha, val);
			if (beta <= alpha) {
				break;
			}
		}
		return maxVal;
	}

	/**
	 * 博弈树 Min 节点
	 */
	private int minNode(IGameCheck gc, int[][] grid, int depth, int alpha, int beta, int aiColor, int humanColor) {
		if (depth == 0 || isTimeUp) {
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		if (System.currentTimeMillis() - searchStartTime > timeLimitMs) {
			isTimeUp = true;
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		// 只防守最危险的 4~6 个点
		int count = (depth >= 7) ? 4 : ((depth >= 5) ? 6 : ((depth >= 3) ? 10 : 14));
		List<MoveScore> moves = generateSortedMoves(grid, humanColor, aiColor, count);
		if (moves.isEmpty()) return 0;
		int minVal = Integer.MAX_VALUE;

		for (MoveScore m : moves) {
			grid[m.x][m.y] = humanColor;
			if (gc.checkEnd(m.x, m.y, humanColor)) {
				grid[m.x][m.y] = 0;
				return -FIVE + (10 - depth) * 10000;
			}
			int val = maxNode(gc, grid, depth - 1, alpha, beta, aiColor, humanColor);
			grid[m.x][m.y] = 0;
			minVal = Math.min(minVal, val);
			beta = Math.min(beta, val);
			if (beta <= alpha) {
				break;
			}
		}
		return minVal;
	}

	/**
	 * 棋盘全局局面静态评估函数
	 */
	private int evaluateBoardGlobal(int[][] grid, int aiColor, int humanColor) {
		int aiScore = 0;
		int humanScore = 0;
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] == aiColor) {
					aiScore += evaluateStoneAt(grid, i, j, aiColor) + POS_VALUE[i][j];
				} else if (grid[i][j] == humanColor) {
					humanScore += evaluateStoneAt(grid, i, j, humanColor) + POS_VALUE[i][j];
				}
			}
		}
		return (int)(aiScore * 1.0 - humanScore * 1.6);
	}

	/**
	 * 生成并排序当前下法
	 */
	private List<MoveScore> generateSortedMoves(int[][] grid, int myCol, int oppCol, int maxCount) {
		List<MoveScore> list = new ArrayList<>();
		int searchDist = (maxCount <= 10) ? 1 : 2;

		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0 || !hasNeighbor(grid, i, j, searchDist)) continue;
				int atk = evaluatePointOnBoard(grid, i, j, myCol);
				int def = evaluatePointOnBoard(grid, i, j, oppCol);
				if (atk >= FIVE) {
					list.clear();
					list.add(new MoveScore(i, j, Integer.MAX_VALUE));
					return list;
				}
				if (def >= FIVE) {
					list.clear();
					list.add(new MoveScore(i, j, Integer.MAX_VALUE - 1));
					return list;
				}
				int score;
				if (def >= GOOD_FOUR) {
					score = def * 5;
				} else if (atk >= GOOD_FOUR) {
					score = atk * 4;
				} else if (def >= GOOD_THREE) {
					score = (int)(def * 3.0 + atk);
				} else if (atk >= GOOD_THREE) {
					score = (int)(atk * 2.5 + def);
				} else {
					score = (int)(atk * 1.0 + def * 1.4) + POS_VALUE[i][j] * 4;
				}
				list.add(new MoveScore(i, j, score));
			}
		}
		Collections.sort(list, (a, b) -> Integer.compare(b.score, a.score));
		int limit = Math.min(maxCount, list.size());
		return new ArrayList<>(list.subList(0, limit));
	}

	/**
	 * 评估棋盘上某点落子后的局部分
	 */
	private int evaluatePointOnBoard(int[][] grid, int x, int y, int color) {
		grid[x][y] = color;
		int score = evaluateStoneAt(grid, x, y, color);
		grid[x][y] = 0;
		return score;
	}

	/**
	 * 评估单颗棋子在四个方向上的棋型得分
	 */
	private int evaluateStoneAt(int[][] grid, int x, int y, int color) {
		int total = 0;
		int goodThree = 0;
		int fourCount = 0;
		for (int d = 0; d < 4; d++) {
			int p = analyzeLineScore(grid, x, y, color, DIR[d][0], DIR[d][1]);
			total += p;
			if (p >= GOOD_FOUR || p >= BAD_FOUR) fourCount++;
			else if (p >= GOOD_THREE) goodThree++;
		}
		if (fourCount >= 2 || (fourCount >= 1 && goodThree >= 1)) {
			total += 500000;
		} else if (goodThree >= 2) {
			total += 250000;
		}
		return total;
	}

	/**
	 * 棋形匹配匹配函数
	 */
	private boolean match(char[] buf, String pat) {
		int m = pat.length();
		for (int i = 0; i <= 9 - m; i++) {
			boolean ok = true;
			for (int j = 0; j < m; j++) {
				if (buf[i + j] != pat.charAt(j)) {
					ok = false;
					break;
				}
			}
			if (ok) return true;
		}
		return false;
	}

	/**
	 * 棋型匹配，1 己方，0 空位，- 敌方，# 出界
	 */
	private int analyzeLineScore(int[][] grid, int x, int y, int color, int dx, int dy) {
		int idx = 0;
		for (int i = -4; i <= 4; i++) {
			int nx = x + i * dx;
			int ny = y + i * dy;
			if (nx < 0 || nx >= GameCheck.SIZE || ny < 0 || ny >= GameCheck.SIZE) {
				lineBuf[idx++] = '#';
			} else {
				int val = grid[nx][ny];
				if (val == color) lineBuf[idx++] = '1';
				else if (val == 0) lineBuf[idx++] = '0';
				else lineBuf[idx++] = '-';
			}
		}

		if (match(lineBuf, "11111")) return FIVE;
		if (match(lineBuf, "011110")) return GOOD_FOUR;
		if (match(lineBuf, "01111-") || match(lineBuf, "-11110") || match(lineBuf, "01111#") || match(lineBuf, "#11110") ||
		    match(lineBuf, "10111") || match(lineBuf, "11011") || match(lineBuf, "11101")) return BAD_FOUR;
		if (match(lineBuf, "01110") || match(lineBuf, "010110") || match(lineBuf, "011010")) return GOOD_THREE;
		if (match(lineBuf, "00111-") || match(lineBuf, "-11100") || match(lineBuf, "00111#") || match(lineBuf, "#11100") ||
		    match(lineBuf, "01011-") || match(lineBuf, "-11010") || match(lineBuf, "01101-") || match(lineBuf, "-10110") ||
		    match(lineBuf, "10011") || match(lineBuf, "11001") || match(lineBuf, "10101")) return BAD_THREE;
		if (match(lineBuf, "00110") || match(lineBuf, "01100") || match(lineBuf, "01010") || match(lineBuf, "010010") || match(lineBuf, "001010") || match(lineBuf, "010100")) return GOOD_TWO;
		if (match(lineBuf, "00011-") || match(lineBuf, "-11000") || match(lineBuf, "00011#") || match(lineBuf, "#11000") ||
		    match(lineBuf, "00101-") || match(lineBuf, "-10100") || match(lineBuf, "01001-") || match(lineBuf, "-10010")) return BAD_TWO;
		return 0;
	}

	/**
	 * 判断目标位置指定曼哈顿距离内是否已有棋子
	 */
	private boolean hasNeighbor(int[][] grid, int x, int y, int dist) {
		int minX = Math.max(0, x - dist), maxX = Math.min(GameCheck.SIZE - 1, x + dist);
		int minY = Math.max(0, y - dist), maxY = Math.min(GameCheck.SIZE - 1, y + dist);
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {
				if (grid[i][j] != 0) return true;
			}
		}
		return false;
	}

	/**
	 * 封装位置和分数
	 */
	private static class MoveScore {
		int x, y, score;
		MoveScore(int x, int y, int score) {
			this.x = x;
			this.y = y;
			this.score = score;
		}
	}
}