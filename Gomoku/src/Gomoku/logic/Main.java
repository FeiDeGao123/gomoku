package Gomoku.logic;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MainCheck logic = new MainCheck();
        Scanner scanner = new Scanner(System.in);
        int currColor = MainCheck.BLACK;
        printBoard(logic.getGrid());
        while (true) {
            String colName = (currColor == MainCheck.BLACK) ? "黑棋(X)" : "白棋(O)";
            System.out.println("轮到 " + colName + " 落子，请输入行和列 (空格隔开): ");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            if (!logic.checkPush(x, y, currColor)) {
                System.out.println("落子无效\n");
                continue;
            }
            printBoard(logic.getGrid());
            if (logic.checkEnd(x, y, currColor)) {
                System.out.println(colName + " 获胜");
                break;
            }
            currColor = (currColor == MainCheck.BLACK) ? MainCheck.WHITE : MainCheck.BLACK;
        }
        scanner.close();
    }

    public static void printBoard(int[][] grid) {
        System.out.print("   ");
        for (int i = 0; i < MainCheck.SIZE; i++) {
            System.out.printf("%2d ", i);
        }
        System.out.println();
        for (int i = 0; i < MainCheck.SIZE; i++) {
            System.out.printf("%2d ", i);
            
            for (int j = 0; j < MainCheck.SIZE; j++) {
                if (grid[i][j] == 0) {
                    System.out.print(" + ");
                } else if (grid[i][j] == MainCheck.BLACK) {
                    System.out.print(" X ");
                } else if (grid[i][j] == MainCheck.WHITE) {
                    System.out.print(" O ");
                }
            }
            System.out.println();
        }
    }
}