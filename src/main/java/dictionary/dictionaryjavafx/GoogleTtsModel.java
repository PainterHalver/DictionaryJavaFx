package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * https://translate.googleapis.com/translate_tts?client=gtx&ie=UTF-8&tl=en&q=hello
 */
public class GoogleTtsModel {
  public static void speak(String query) {
    AudioInputStream din = null;
    try {
      AudioInputStream in = AudioSystem.getAudioInputStream(new URL("https://translate.googleapis.com/translate_tts?client=gtx&ie=UTF-8&tl=en&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)));
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

  public static void main(String[] args) {
    speak("hello world this is google speaking");
  }
}
