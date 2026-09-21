package gomoku.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * 游戏多媒体音频播放工具类
 * 负责五子棋落子打击音效
 */
public class SoundPlayer {
    private static volatile boolean soundEnabled = true;

    /**
     * 检查当前落子音效是否处于启用状态
     * @return true 表示已启用，false 表示已静音
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * 设置全局落子音效启用状态
     * @param enabled true开启，false关闭
     */
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    /**
     * 播放落子打击音效
     */
    public static void playDrop() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                InputStream in = SoundPlayer.class.getResourceAsStream("/drop.wav");
                try (BufferedInputStream bis = new BufferedInputStream(in);
                     AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    clip.start();
                }
            } catch (Exception e) {}
        }).start();
    }
}