package gomoku.logic;

/**
 * 五子棋规则判定与棋盘状态抽象接口
 */
public interface IGameCheck {
    int SIZE = 15;
    int BLACK = 1;
    int WHITE = 2;
    
    /**
	 * 检查坐标是否处于棋盘合法有效范围内
	 * @param x 行坐标
	 * @param y 列坐标
	 * @return 若在 [0, SIZE-1] 范围内返回 true，超出棋盘边界返回 false
	 */
    boolean checkBound(int x, int y);
    
    /**
	 * 执行落子尝试与合法性校验
	 * @param x 行坐标
	 * @param y 列坐标
	 * @param color 棋子颜色
	 * @return 落子成功返回 true，若越界或该位置已有棋子则返回 false
	 */
    boolean checkPush(int x, int y, int color);
    
    /**
	 * 计算若在 (x, y) 处落子，其在四个方向轴线上能形成的同色连续棋子最大长度
	 * 以当前点为基准，向正反两个方向发射射线
	 * @param x 基准点行坐标
	 * @param y 基准点列坐标
	 * @param color 待判定的棋子颜色
	 * @return 四个方向中连续同色棋子的最大数量
	 */
    int countMax(int x, int y, int color);
    
    /**
	 * 检查当前落子是否达成获胜条件
	 * @param x 落子行坐标
	 * @param y 落子列坐标
	 * @param color 落子颜色
	 * @return 若形成 5 颗或 5 颗以上同色连续棋子返回 true，否则返回 false
	 */
    boolean checkEnd(int x, int y, int color);
    
    /**
	 * 弹出栈顶最新落子，还原棋盘对应格点状态
	 * @return 被撤销落子的坐标 [x, y]；若栈为空返回 null
	 */
    int[] stkPop();
    
    /**
	 * 查看最后落子坐标
	 * @return 栈顶最新落子坐标 [x, y]；若栈为空返回 null
	 */
    int[] stkTop();
    
    /**
	 * 获取当前对局已落下的总手数
	 * @return 栈内元素总数
	 */
    int stkSize();
    
    /**
	 * 检查是否已下满
	 * @return 若落子数达到 15×15=225 返回 true，否则返回 false
	 */
    boolean allPushed();
    
    /**
	 * 重置棋盘状态为全新空开局，清空二维网格矩阵并将落子历史栈清空
	 */
    void reset();
    
    /**
	 * 获取棋盘矩阵引用
	 * @return 15×15 的底层 int 数组
	 */
    int[][] getGrid();
}