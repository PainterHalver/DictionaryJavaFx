package dictionary.dictionaryjavafx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * https://stackoverflow.com/questions/8147284/how-to-use-google-translate-api-in-my-java-application
 * QUOTAS: 5000 calls
 */
public class GoogleScriptModel {

  public static String translate(String langFrom, String langTo, String text) throws IOException {
    // INSERT YOU URL HERE
    String urlStr = Constants.GOOGLE_SCRIPT_URL +
        "?q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
        "&target=" + langTo +
        "&source=" + langFrom;
    URL url = new URL(urlStr);
    StringBuilder response = new StringBuilder();
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestProperty("User-Agent", "Mozilla/5.0");
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    return response.toString();
  }
}
