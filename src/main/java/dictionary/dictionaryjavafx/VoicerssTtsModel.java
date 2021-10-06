package dictionary.dictionaryjavafx;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * http://www.voicerss.org/
 * https://stackoverflow.com/questions/13789063/get-sound-from-a-url-with-java
 * QUOTAS: 350 daily requests
 * http://api.voicerss.org/?key=3b347642874c46b8a91b3b6373ad9916&hl=en-us&v=Mary&src=hello
 */
public class VoicerssTtsModel {
  public static void speak(String query) {
    AudioInputStream din = null;
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
}
