package gomoku.logic;

/**
 * 人工智能算法抽象接口
 */
public interface IGameAI {
	int EASY = 1;
    int MID = 2;
    int HARD = 3;
    
    /**
	 * 寻找最佳落子点
	 * @param gc 逻辑判断对象
	 * @param aiColor AI 执子颜色
	 * @return 最佳落子坐标 [x, y]
	 */
    int[] findMove(IGameCheck gc, int aiColor);
    
    /**
	 * 寻找最佳落子点
	 * @param gc 逻辑判断对象
	 * @param aiColor AI 执子颜色
	 * @param level 难度等级 (EASY / MID / HARD)
	 * @return 最佳落子坐标 [x, y]
	 */
    int[] findMove(IGameCheck gc, int aiColor, int level);
}