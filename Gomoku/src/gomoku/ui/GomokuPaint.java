package gomoku.ui;

import java.awt.*;
import gomoku.logic.GameCheck;

public class GomokuPaint {
    public static void drawGrid(Graphics2D g2d, int edge, int sq, int dot) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(new Color(60, 40, 20));
        g2d.setStroke(new BasicStroke(1.2f));
        for (int i = 0; i < GameCheck.SIZE; i++) {
            g2d.drawLine(edge, edge + i * sq, edge + (GameCheck.SIZE - 1) * sq, edge + i * sq);
            g2d.drawLine(edge + i * sq, edge, edge + i * sq, edge + (GameCheck.SIZE - 1) * sq);
        }
        int[][] stars = {{7, 7}, {3, 3}, {11, 3}, {3, 11}, {11, 11}};
        for (int[] s : stars) {
            int cx = edge + s[0] * sq - dot;
            int cy = edge + s[1] * sq - dot;
            g2d.fillOval(cx, cy, dot * 2, dot * 2);
        }
    }

    public static void drawPiece(Graphics2D g2d, int cx, int cy, int radius, boolean isBlack) {
        g2d.setColor(new Color(0, 0, 0, 70));
        g2d.fillOval(cx - radius + 2, cy - radius + 3, radius * 2, radius * 2);
        if (isBlack) {
            RadialGradientPaint blackRgp = new RadialGradientPaint(
                cx - radius * 0.35f, cy - radius * 0.35f, radius * 1.5f,
                new float[]{0.0f, 0.45f, 1.0f},
                new Color[]{new Color(110, 110, 110), new Color(40, 40, 40), new Color(15, 15, 15)}
            );
            g2d.setPaint(blackRgp);
        } else {
            RadialGradientPaint whiteRgp = new RadialGradientPaint(
                cx - radius * 0.35f, cy - radius * 0.35f, radius * 1.4f,
                new float[]{0.0f, 0.6f, 1.0f},
                new Color[]{new Color(255, 255, 255), new Color(230, 230, 235), new Color(175, 175, 180)}
            );
            g2d.setPaint(whiteRgp);
        }
        g2d.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);
        if (!isBlack) {
            g2d.setColor(new Color(150, 150, 150, 80));
            g2d.setStroke(new BasicStroke(0.8f));
            g2d.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        }
    }

    public static void drawDot(Graphics2D g2d, int cx, int cy, int dot) {
        g2d.setColor(new Color(235, 20, 20));
        g2d.fillOval(cx - dot, cy - dot, dot * 2, dot * 2);
    }
}