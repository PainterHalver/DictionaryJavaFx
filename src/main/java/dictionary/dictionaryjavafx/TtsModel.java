package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


public class TtsModel {
  private static AudioInputStream din = null;
  private static SourceDataLine line = null;
  private static boolean stopped = false;

  /**
   * https://translate.googleapis.com/translate_tts?client=gtx&ie=UTF-8&tl=en&q=hello
   */
  public static void googleTss(String query) {
    if (Objects.equals(query, "")) return;
//    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//    for (Thread x : threadSet) {
//      if (Objects.equals(x.getName(), "Google TTS Thread")) x.stop();
//    }
    Thread testThread = new Thread(() -> {
      try {
        AudioInputStream in = AudioSystem.getAudioInputStream(
            new URL(Constants.GOOGLE_TTS_URL + URLEncoder.encode(query, StandardCharsets.UTF_8)));
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



//  public static void main(String[] args) throws InterruptedException {
//
//    googleTss("hello world this is google speaking");
//    Thread.sleep(1000);
//    voicerssTss("hello world this is voicer speaking");
//  }
}
