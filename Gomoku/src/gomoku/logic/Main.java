package gomoku.logic;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameCheck gc = new GameCheck();
        Scanner scanner = new Scanner(System.in);
        int currColor = GameCheck.BLACK;
        printBoard(gc.getGrid());
        while (true) {
            String colName = (currColor == GameCheck.BLACK) ? "黑棋(X)" : "白棋(O)";
            System.out.println("轮到 " + colName + " 落子，请输入行和列 (空格隔开): ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            if (!gc.checkPush(x, y, currColor)) {
                System.out.println("落子无效\n");
                continue;
            }
            printBoard(gc.getGrid());
            if (gc.checkEnd(x, y, currColor)) {
                System.out.println(colName + " 获胜");
                break;
            }
            currColor = (currColor == GameCheck.BLACK) ? GameCheck.WHITE : GameCheck.BLACK;
        }
        scanner.close();
    }

    public static void printBoard(int[][] grid) {
        System.out.print("   ");
        for (int i = 0; i < GameCheck.SIZE; i++) {
            System.out.printf("%2d ", i);
        }
        System.out.println();
        for (int i = 0; i < GameCheck.SIZE; i++) {
            System.out.printf("%2d ", i);
            
            for (int j = 0; j < GameCheck.SIZE; j++) {
                if (grid[i][j] == 0) {
                    System.out.print(" + ");
                } else if (grid[i][j] == GameCheck.BLACK) {
                    System.out.print(" X ");
                } else if (grid[i][j] == GameCheck.WHITE) {
                    System.out.print(" O ");
                }
            }
            System.out.println();
        }
    }
}