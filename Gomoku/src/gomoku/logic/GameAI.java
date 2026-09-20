package gomoku.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 五子棋人机对战类
 * 简单难度：贪心算法，人类思维
 * 中等难度：评估表的启发式评估
 * 困难难度： 极小化极大博弈，Alpha-Beta 剪枝，迭代加深搜索
 */
public class GameAI {
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
	private long timeLimitMs = 5000;
	private boolean isTimeUp = false;

	/**
	 * 寻找最佳落子点
	 * @param gc 棋盘裁决对象
	 * @param aiColor AI 执子颜色
	 * @return 最佳落子坐标 [x, y]
	 */
	public int[] findMove(GameCheck gc, int aiColor) {
		return findMove(gc, aiColor, EASY);
	}

	/**
	 * 寻找最佳落子点
	 * @param gc 棋盘裁决对象
	 * @param aiColor AI 执子颜色
	 * @param level 难度等级 (EASY / MID / HARD)
	 * @return 最佳落子坐标 [x, y]
	 */
	public int[] findMove(GameCheck gc, int aiColor, int level) {
		if (level == HARD) {
			return findMoveHard(gc, aiColor);
		} else if (level == MID) {
			return findMoveMid(gc, aiColor);
		}
		return findMoveEasy(gc, aiColor);
	}

	/**
	 * 简单难度 AI，贪心落子
	 * 优先占天元，从连五到连二倒序扫描，优先自己连线，其次封堵对手连线
	 */
	private int[] findMoveEasy(GameCheck gc, int aiColor) {
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
	 * 位置矩阵加权，对人类活三、活四的防守拦截，相对于简单略强
	 */
	private int[] findMoveMid(GameCheck gc, int aiColor) {
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
	 * 业余玩家几乎无解
	 */
	private int[] findMoveHard(GameCheck gc, int aiColor) {
		int humanColor = aiColor ^ 3;
		int[][] grid = gc.getGrid();
		if (grid[7][7] == 0) return new int[]{7, 7};
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
		// 生成候选点并按静态得分降序排序，只取前 36 个点
		List<MoveScore> candidateMoves = generateSortedMoves(grid, aiColor, humanColor, 36);
		if (candidateMoves.isEmpty()) return findMoveMid(gc, aiColor);
		int[] bestMove = new int[]{candidateMoves.get(0).x, candidateMoves.get(0).y};
		// 迭代加深搜索，从 2 层递增探测至 6 层
		for (int depth = 2; depth <= 6; depth += 2) {
			int currentBestX = bestMove[0];
			int currentBestY = bestMove[1];
			int bestVal = Integer.MIN_VALUE;
			int alpha = -50000000;
			int beta = 50000000;
			for (MoveScore m : candidateMoves) {
				// 模拟下子
				grid[m.x][m.y] = aiColor;
				if (gc.checkEnd(m.x, m.y, aiColor)) {
					grid[m.x][m.y] = 0;
					return new int[]{m.x, m.y};
				}
				// 进入 Min 节点模拟人类
				int val = minNode(gc, grid, depth - 1, alpha, beta, aiColor, humanColor);
				grid[m.x][m.y] = 0; //回溯
				if (System.currentTimeMillis() - searchStartTime > timeLimitMs) {
					isTimeUp = true;
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
	 * 最大化己方收益，Alpha 剪枝
	 */
	private int maxNode(GameCheck gc, int[][] grid, int depth, int alpha, int beta, int aiColor, int humanColor) {
		// 深度达到叶子节点或超时，返回静态全局局势评分
		if (depth == 0 || isTimeUp) {
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		if (System.currentTimeMillis() - searchStartTime > timeLimitMs) {
			isTimeUp = true;
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		// 取前 14 个最有价值的下法
		List<MoveScore> moves = generateSortedMoves(grid, aiColor, humanColor, 14);
		if (moves.isEmpty()) return 0;
		int maxVal = Integer.MIN_VALUE;
		for (MoveScore m : moves) {
			grid[m.x][m.y] = aiColor;
			if (gc.checkEnd(m.x, m.y, aiColor)) {
				grid[m.x][m.y] = 0;
				return FIVE - (6 - depth) * 10000; // 步数衰减惩罚
			}
			int val = minNode(gc, grid, depth - 1, alpha, beta, aiColor, humanColor);
			grid[m.x][m.y] = 0;
			maxVal = Math.max(maxVal, val);
			alpha = Math.max(alpha, val);
			// 若当前分支价值已超过对手允许的最大上限，直接剪枝
			if (beta <= alpha) {
				break;
			}
		}
		return maxVal;
	}

	/**
	 * 博弈树 Min 节点
	 * 假设人类高手会采取最优防守，最小化 AI 收益，实施 Beta 剪枝
	 */
	private int minNode(GameCheck gc, int[][] grid, int depth, int alpha, int beta, int aiColor, int humanColor) {
		if (depth == 0 || isTimeUp) {
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		if (System.currentTimeMillis() - searchStartTime > timeLimitMs) {
			isTimeUp = true;
			return evaluateBoardGlobal(grid, aiColor, humanColor);
		}
		List<MoveScore> moves = generateSortedMoves(grid, humanColor, aiColor, 14);
		if (moves.isEmpty()) return 0;
		int minVal = Integer.MAX_VALUE;
		for (MoveScore m : moves) {
			grid[m.x][m.y] = humanColor;
			if (gc.checkEnd(m.x, m.y, humanColor)) {
				grid[m.x][m.y] = 0;
				return -FIVE + (6 - depth) * 10000;
			}
			int val = maxNode(gc, grid, depth - 1, alpha, beta, aiColor, humanColor);
			grid[m.x][m.y] = 0;
			minVal = Math.min(minVal, val);
			beta = Math.min(beta, val);
			// 若当前分支价值低于己方已知保底收益，直接剪枝
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
		return (int)(aiScore * 1.05 - humanScore * 1.35);
	}

	/**
	 * 生成并排序当前下法
	 */
	private List<MoveScore> generateSortedMoves(int[][] grid, int myCol, int oppCol, int maxCount) {
		List<MoveScore> list = new ArrayList<>();
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (grid[i][j] != 0 || !hasNeighbor(grid, i, j, 2)) continue;
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
					score = (int)(atk * 1.0 + def * 1.35) + POS_VALUE[i][j] * 4;
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
	 * 棋型匹配，1 己方，0 空位，- 敌方，# 出界
	 */
	private int analyzeLineScore(int[][] grid, int x, int y, int color, int dx, int dy) {
		StringBuilder sb = new StringBuilder(9);
		for (int i = -4; i <= 4; i++) {
			int nx = x + i * dx;
			int ny = y + i * dy;
			if (nx < 0 || nx >= GameCheck.SIZE || ny < 0 || ny >= GameCheck.SIZE) {
				sb.append('#');
			} else {
				if (grid[nx][ny] == color) sb.append('1');
				else if (grid[nx][ny] == 0) sb.append('0');
				else sb.append('-');
			}
		}
		String s = sb.toString();
		if (s.contains("11111")) return FIVE;
		if (s.contains("011110")) return GOOD_FOUR;
		if (s.contains("01111-") || s.contains("-11110") || s.contains("01111#") || s.contains("#11110") ||
		    s.contains("10111") || s.contains("11011") || s.contains("11101")) return BAD_FOUR;
		if (s.contains("01110") || s.contains("010110") || s.contains("011010")) return GOOD_THREE;
		if (s.contains("00111-") || s.contains("-11100") || s.contains("00111#") || s.contains("#11100") ||
		    s.contains("01011-") || s.contains("-11010") || s.contains("01101-") || s.contains("-10110") ||
		    s.contains("10011") || s.contains("11001") || s.contains("10101")) return BAD_THREE;
		if (s.contains("00110") || s.contains("01100") || s.contains("01010") || s.contains("010010") || s.contains("001010") || s.contains("010100")) return GOOD_TWO;
		if (s.contains("00011-") || s.contains("-11000") || s.contains("00011#") || s.contains("#11000") ||
		    s.contains("00101-") || s.contains("-10100") || s.contains("01001-") || s.contains("-10010")) return BAD_TWO;
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