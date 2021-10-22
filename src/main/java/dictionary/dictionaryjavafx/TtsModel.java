package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TtsModel {
  private static AudioInputStream din = null;
  private static SourceDataLine line = null;
  private static boolean stopped = false;

  /**
   * https://translate.googleapis.com/translate_tts?client=gtx&ie=UTF-8&tl=en&q=hello
   */
  public static void apiTTS(String query, String baseURL) {
    if (Objects.equals(query, "")) return;
//    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//    for (Thread x : threadSet) {
//      if (Objects.equals(x.getName(), "Google TTS Thread")) x.stop();
//    }
    Thread testThread = new Thread(() -> {
      try {
        AudioInputStream in = AudioSystem.getAudioInputStream(
            new URL(baseURL + URLEncoder.encode(query, StandardCharsets.UTF_8)));
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
            baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
            false);
        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
        line = (SourceDataLine) AudioSystem.getLine(info);
        if (line != null) {
          line.open(decodedFormat);
          byte[] data = new byte[4096];
          // Start
          line.start();

          int nBytesRead;
          while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
            line.write(data, 0, nBytesRead);
          }
          // Stop
          line.drain();
          line.stop();
          line.close();
          din.close();
        }

      } catch (Exception e) {
        e.printStackTrace();
        Platform.runLater(() -> {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("");
          alert.setContentText(Constants.NO_INTERNET);
          Optional<ButtonType> option = alert.showAndWait();
        });
      } finally {
        if (din != null) {
          try {
            din.close();
          } catch (IOException ignored) {
          }
        }
      }
    });
    testThread.setName("Google TTS Thread");
    testThread.start();
  }

  /**
   * http://www.voicerss.org/
   * https://stackoverflow.com/questions/13789063/get-sound-from-a-url-with-java
   * QUOTAS: 350 daily requests
   * http://api.voicerss.org/?key=3b347642874c46b8a91b3b6373ad9916&hl=en-us&v=Mary&src=hello
   */
    public static void voicerssTss(String query) {
      try {
        AudioInputStream in = AudioSystem.getAudioInputStream(new URL(Constants.VOICERSS_TTS_URL + URLEncoder.encode(query, StandardCharsets.UTF_8)));
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
            baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
            false);
        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        if(line != null) {
          line.open(decodedFormat);
          byte[] data = new byte[4096];
          // Start
          line.start();

          int nBytesRead;
          while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
            line.write(data, 0, nBytesRead);
          }
          // Stop
          line.drain();
          line.stop();
          line.close();
          din.close();
        }

      }
      catch(Exception e) {
        e.printStackTrace();
      }
      finally {
        if(din != null) {
          try { din.close(); } catch(IOException ignored) { }
        }
      }
    }

  public static void freeTts(String query) {
    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
    Voice voice;//Creating object of Voice class
    voice = VoiceManager.getInstance().getVoice("kevin");//Getting voice
    if (voice != null) {
      voice.allocate();//Allocating Voice
    }
    try {
      voice.setRate(190);//Setting the rate of the voice
      voice.setPitch(150);//Setting the Pitch of the voice
      voice.setVolume(2);//Setting the volume of the voice
      voice.speak(query);//Calling speak() method
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
}
