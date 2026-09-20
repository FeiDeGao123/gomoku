package gomoku.logic;

import java.util.Stack;

/**
 * 五子棋判定类
 * 负责棋盘网格、落子合法校验、连珠判定、平局检测、悔棋
 */
public class GameCheck {
	public static final int SIZE = 15;
	public static final int BLACK = 1;
	public static final int WHITE = 2;
	private static final int dx[] = {-1, -1, 0, 1};
	private static final int dy[] = {0, 1, 1, 1};
	private int grid[][];
	private Stack<int[]> stk = new Stack<>();
	
	/**
	 * 构造方法，初始化 15×15 空棋盘
	 */
	public GameCheck() {
		grid = new int[SIZE][SIZE];
	}
	
	/**
	 * 检查坐标是否处于棋盘合法有效范围内
	 * @param x 行坐标
	 * @param y 列坐标
	 * @return 若在 [0, SIZE-1] 范围内返回 true，超出棋盘边界返回 false
	 */
	public boolean checkBound(int x, int y) {
		return !(x < 0 || x >= SIZE || y < 0 || y >= SIZE);
	}
	
	/**
	 * 执行落子尝试与合法性校验
	 * @param x 行坐标
	 * @param y 列坐标
	 * @param color 棋子颜色
	 * @return 落子成功返回 true，若越界或该位置已有棋子则返回 false
	 */
	public boolean checkPush(int x, int y, int color) {
		if (!checkBound(x, y) || grid[x][y] != 0) return false;
		grid[x][y] = color;
		stk.push(new int[]{x, y});
		return true;
	}
	
	/**
	 * 计算若在 (x, y) 处落子，其在四个方向轴线上能形成的同色连续棋子最大长度
	 * 以当前点为基准，向正反两个方向发射射线
	 * @param x 基准点行坐标
	 * @param y 基准点列坐标
	 * @param color 待判定的棋子颜色
	 * @return 四个方向中连续同色棋子的最大数量
	 */
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
	
	/**
	 * 检查当前落子是否达成获胜条件
	 * @param x 落子行坐标
	 * @param y 落子列坐标
	 * @param color 落子颜色
	 * @return 若形成 5 颗或 5 颗以上同色连续棋子返回 true，否则返回 false
	 */
	public boolean checkEnd(int x, int y, int color) {
        return countMax(x, y, color) >= 5;
	}
	
	/**
	 * 弹出栈顶最新落子，还原棋盘对应格点状态
	 * @return 被撤销落子的坐标 [x, y]；若栈为空返回 null
	 */
	public int[] stkPop() {
		if (stk.isEmpty()) return null;
		int[] pre = stk.pop();
		grid[pre[0]][pre[1]] = 0;
		return pre;
	}
	
	/**
	 * 查看最后落子坐标
	 * @return 栈顶最新落子坐标 [x, y]；若栈为空返回 null
	 */
	public int[] stkTop() {
		if (stk.isEmpty()) return null;
		return stk.peek();
	}
	
	/**
	 * 获取当前对局已落下的总手数
	 * @return 栈内元素总数
	 */
	public int stkSize() {
		return stk.size();
	}
	
	/**
	 * 检查是否已下满
	 * @return 若落子数达到 15×15=225 返回 true，否则返回 false
	 */
	public boolean allPushed() {
		return stkSize() >= SIZE * SIZE;
	}
	
	/**
	 * 重置棋盘状态为全新空开局，清空二维网格矩阵并将落子历史栈清空
	 */
	public void reset() {
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            grid[i][j] = 0;
	        }
	    }
	    stk.clear();
	}
	
	/**
	 * 获取棋盘矩阵引用
	 * @return 15×15 的底层 int 数组
	 */
	public int[][] getGrid() {
		return grid;
	}
}
