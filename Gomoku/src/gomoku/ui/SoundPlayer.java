package gomoku.ui;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundPlayer {
    private static volatile boolean soundEnabled = true;

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

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