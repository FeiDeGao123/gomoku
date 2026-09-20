package gomoku.logic;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * 测试覆盖：边界、坐标重叠非法落子、四个方向(横/竖/正斜/反斜)连珠、悔棋栈进出时序、全盘下满平局判定及重置生命周期
 */
public class TestGameCheck {
	private GameCheck gc;
	@Before
	public void setUp() {
		gc = new GameCheck();
	}
	/**
	 * 测试构造方法：验证初始全盘为空、步数为0、栈为空
	 */
	@Test
	public void testGameCheck() {
		assertNotNull("棋盘实例不应为空", gc);
		assertEquals("初始步数应为0", 0, gc.stkSize());
		assertNull("空棋盘查看栈顶应返回null", gc.stkTop());
		assertNull("空棋盘弹出栈顶应返回null", gc.stkPop());
		assertFalse("初始状态不应是满盘", gc.allPushed());
		
		int[][] grid = gc.getGrid();
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				assertEquals("初始每个格子必须是0(空位)", 0, grid[i][j]);
			}
		}
	}
	/**
	 * 越界检查
	 * 覆盖四个边界角点、中心点、负数坐标以及超界坐标
	 */
	@Test
	public void testCheckBound() {
		assertTrue("天元坐标(7,7)应合法", gc.checkBound(7, 7));
		assertTrue("左上角(0,0)应合法", gc.checkBound(0, 0));
		assertTrue("右上角(0,14)应合法", gc.checkBound(0, 14));
		assertTrue("左下角(14,0)应合法", gc.checkBound(14, 0));
		assertTrue("右下角(14,14)应合法", gc.checkBound(14, 14));
		assertFalse("负数行坐标(-1,5)应非法", gc.checkBound(-1, 5));
		assertFalse("负数列坐标(5,-1)应非法", gc.checkBound(5, -1));
		assertFalse("双负数坐标(-1,-1)应非法", gc.checkBound(-1, -1));
		assertFalse("上越界行坐标(15,5)应非法", gc.checkBound(15, 5));
		assertFalse("上越界列坐标(5,15)应非法", gc.checkBound(5, 15));
		assertFalse("双上越界坐标(15,15)应非法", gc.checkBound(15, 15));
		assertFalse("极端远距离坐标(999,999)应非法", gc.checkBound(999, 999));
	}
	/**
	 * 落子逻辑
	 * 验证正常落子、重复位置落子拦截、以及越界落子拦截
	 */
	@Test
	public void testCheckPush() {
		assertTrue("在空位(7,7)落黑子应成功", gc.checkPush(7, 7, GameCheck.BLACK));
		assertEquals("棋盘对应坐标状态应记录为黑子", GameCheck.BLACK, gc.getGrid()[7][7]);
		assertEquals("落子后手数应变为1", 1, gc.stkSize());
		assertFalse("在已有棋子的(7,7)再次落白子应被拦截", gc.checkPush(7, 7, GameCheck.WHITE));
		assertEquals("棋盘(7,7)应仍保持最初的黑子", GameCheck.BLACK, gc.getGrid()[7][7]);
		assertEquals("非法落子不应导致步数增加", 1, gc.stkSize());
		assertFalse("向越界位置(-1,0)落子应失败", gc.checkPush(-1, 0, GameCheck.BLACK));
		assertFalse("向越界位置(15,15)落子应失败", gc.checkPush(15, 15, GameCheck.WHITE));
		assertEquals("手数依然保持为1", 1, gc.stkSize());
	}

	/**
	 * 测试延伸算法
	 * 验证单子、连续两子、跨越检测、异色子阻隔情况下的统计正确性
	 */
	@Test
	public void testCountMax() {
		gc.checkPush(7, 7, GameCheck.BLACK);
		assertEquals("孤立落子最大连续数应为1", 1, gc.countMax(7, 7, GameCheck.BLACK));
		gc.checkPush(7, 6, GameCheck.BLACK);
		gc.checkPush(7, 8, GameCheck.BLACK);
		assertEquals("横向三连珠时统计应为3", 3, gc.countMax(7, 7, GameCheck.BLACK));
		assertEquals("端点(7,6)统计应为3", 3, gc.countMax(7, 6, GameCheck.BLACK));
		gc.checkPush(7, 9, GameCheck.WHITE);
		assertEquals("被白棋堵截后黑棋横向最大连续仍是3", 3, gc.countMax(7, 8, GameCheck.BLACK));
		gc.checkPush(6, 7, GameCheck.BLACK);
		assertEquals("多方向中取最大者应为3", 3, gc.countMax(7, 7, GameCheck.BLACK));
	}

	/**
	 * 胜负判定 
	 * 覆盖横向、纵向、正斜对角线、反斜对角线 4 个方位的五连珠与长连(>=5)获胜检测
	 */
	@Test
	public void testCheckEnd() {
		for (int y = 0; y < 4; y++) {
			gc.checkPush(2, y, GameCheck.BLACK);
			assertFalse("4颗子未成五不应判胜", gc.checkEnd(2, y, GameCheck.BLACK));
		}
		gc.checkPush(2, 4, GameCheck.BLACK);
		assertTrue("横向落满5颗同色子必须判胜", gc.checkEnd(2, 4, GameCheck.BLACK));
		gc.reset();
		for (int x = 5; x < 9; x++) {
			gc.checkPush(x, 5, GameCheck.WHITE);
		}
		assertFalse("纵向4颗不应胜", gc.checkEnd(8, 5, GameCheck.WHITE));
		gc.checkPush(9, 5, GameCheck.WHITE);
		assertTrue("纵向第5颗子落下必须判胜", gc.checkEnd(9, 5, GameCheck.WHITE));
		gc.reset();
		gc.checkPush(3, 3, GameCheck.BLACK);
		gc.checkPush(4, 4, GameCheck.BLACK);
		gc.checkPush(5, 5, GameCheck.BLACK);
		gc.checkPush(6, 6, GameCheck.BLACK);
		gc.checkPush(7, 7, GameCheck.BLACK);
		assertTrue("反斜线成五必须判胜", gc.checkEnd(7, 7, GameCheck.BLACK));
		assertTrue("中间位置检测也必须判胜", gc.checkEnd(5, 5, GameCheck.BLACK));
		gc.reset();
		gc.checkPush(8, 2, GameCheck.WHITE);
		gc.checkPush(7, 3, GameCheck.WHITE);
		gc.checkPush(6, 4, GameCheck.WHITE);
		gc.checkPush(5, 5, GameCheck.WHITE);
		gc.checkPush(4, 6, GameCheck.WHITE);
		assertTrue("正斜对角线成五必须判胜", gc.checkEnd(4, 6, GameCheck.WHITE));
		gc.reset();
		gc.checkPush(0, 0, GameCheck.BLACK);
		gc.checkPush(0, 1, GameCheck.WHITE);
		gc.checkPush(0, 2, GameCheck.BLACK);
		gc.checkPush(0, 3, GameCheck.BLACK);
		gc.checkPush(0, 4, GameCheck.BLACK);
		gc.checkPush(0, 5, GameCheck.BLACK);
		assertFalse("被白棋阻隔的4颗黑子不能判胜", gc.checkEnd(0, 5, GameCheck.BLACK));
	}

	/**
	 * 悔棋栈出栈与棋盘状态回退
	 */
	@Test
	public void testStkPop() {
		assertNull("空栈调用 stkPop 应返回 null 且不抛异常", gc.stkPop());
		gc.checkPush(3, 4, GameCheck.BLACK);
		gc.checkPush(5, 6, GameCheck.WHITE);
		assertEquals("当前步数应为2", 2, gc.stkSize());
		int[] pop1 = gc.stkPop();
		assertNotNull("弹出对象不应为空", pop1);
		assertEquals(5, pop1[0]);
		assertEquals(6, pop1[1]);
		assertEquals("棋盘对应坐标必须恢复为0", 0, gc.getGrid()[5][6]);
		assertEquals("悔棋后步数减为1", 1, gc.stkSize());
		int[] pop2 = gc.stkPop();
		assertEquals(3, pop2[0]);
		assertEquals(4, pop2[1]);
		assertEquals("棋盘对应坐标恢复为0", 0, gc.getGrid()[3][4]);
		assertEquals("悔棋后步数减为0", 0, gc.stkSize());
		assertNull(gc.stkPop());
	}

	/**
	 * 查看栈顶元素
	 * 验证栈顶坐标是否能准确反映最后一手，且不破坏栈结构
	 */
	@Test
	public void testStkTop() {
		assertNull("空栈查看栈顶为 null", gc.stkTop());
		gc.checkPush(1, 2, GameCheck.BLACK);
		int[] top1 = gc.stkTop();
		assertNotNull(top1);
		assertEquals(1, top1[0]);
		assertEquals(2, top1[1]);
		assertEquals("stkTop 只看不删，栈大小依然是1", 1, gc.stkSize());
		gc.checkPush(8, 9, GameCheck.WHITE);
		int[] top2 = gc.stkTop();
		assertEquals(8, top2[0]);
		assertEquals(9, top2[1]);
		assertEquals("栈大小变为2", 2, gc.stkSize());
	}

	/**
	 * 累加计数
	 */
	@Test
	public void testStkSize() {
		assertEquals(0, gc.stkSize());
		for (int i = 0; i < 10; i++) {
			gc.checkPush(i, 0, GameCheck.BLACK);
			assertEquals(i + 1, gc.stkSize());
		}
		gc.stkPop();
		assertEquals(9, gc.stkSize());
	}

	/**
	 * 满盘平局
	 * 模拟下满 225 个格子的边界极限场景
	 */
	@Test
	public void testAllPushed() {
		assertFalse("开局不应为满盘", gc.allPushed());
		int step = 0;
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				if (i == GameCheck.SIZE - 1 && j == GameCheck.SIZE - 1) {
					break;
				}
				gc.checkPush(i, j, (step % 2 == 0) ? GameCheck.BLACK : GameCheck.WHITE);
				step++;
			}
		}
		assertEquals(224, gc.stkSize());
		assertFalse("下到224手仍不能判定为满盘", gc.allPushed());
		gc.checkPush(14, 14, GameCheck.BLACK);
		assertEquals(225, gc.stkSize());
		assertTrue("下满 225 手必须判定为 allPushed 成立", gc.allPushed());
	}

	/**
	 * 重置方法
	 * 验证棋盘网格清空与栈历史清空的彻底性
	 */
	@Test
	public void testReset() {
		gc.checkPush(1, 1, GameCheck.BLACK);
		gc.checkPush(2, 2, GameCheck.WHITE);
		gc.checkPush(3, 3, GameCheck.BLACK);
		assertTrue(gc.stkSize() > 0);
		gc.reset();
		assertEquals("重置后步数必须归0", 0, gc.stkSize());
		assertNull("重置后栈顶必须为空", gc.stkTop());
		assertFalse("重置后不可能是满盘", gc.allPushed());
		int[][] grid = gc.getGrid();
		for (int i = 0; i < GameCheck.SIZE; i++) {
			for (int j = 0; j < GameCheck.SIZE; j++) {
				assertEquals("全盘每一个格子都必须重置为0", 0, grid[i][j]);
			}
		}
		assertTrue("重置后之前的位置应能再次落子", gc.checkPush(1, 1, GameCheck.BLACK));
	}

	/**
	 * 获取底层棋盘矩阵
	 */
	@Test
	public void testGetGrid() {
		int[][] grid = gc.getGrid();
		assertNotNull("获取的 grid 引用不应为 null", grid);
		assertEquals("矩阵行数应为 15", GameCheck.SIZE, grid.length);
		assertEquals("矩阵列数应为 15", GameCheck.SIZE, grid[0].length);
		gc.checkPush(5, 5, GameCheck.WHITE);
		assertEquals("通过 getGrid 获取到的值必须与落子颜色严格同步", GameCheck.WHITE, grid[5][5]);
	}
}