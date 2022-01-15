import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.InputStream;

public class SoundPlayer {

    public enum Sound {
        DOORBELL("/doorbell.wav"),
        MEALBELL("/meal_prepared_bell.wav");

        private final String resourcePath;
        private Sound(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public String getResourcePath() {
            return resourcePath;
        }
    }

    public static void playSound(Sound sound) {
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                InputStream resourceStream = SoundPlayer.class.getResourceAsStream(sound.getResourcePath());
                if (resourceStream != null) {
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(resourceStream);
                    clip.open(inputStream);
                    clip.start();
                } else {
                    System.err.println("Unable to load sound!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
