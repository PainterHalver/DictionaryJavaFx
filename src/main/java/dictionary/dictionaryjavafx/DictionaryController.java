package dictionary.dictionaryjavafx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class DictionaryController implements Initializable {
  @FXML
  Button btnTest;

  @FXML
  WebView webView;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    WebEngine webEngine = webView.getEngine();
    webEngine.loadContent("<h1>abaddon</h1><h3><i>/ə'bædən/</i></h3><h2>danh từ</h2><ul><li>âm ti, địa ngục</li><li>con quỷ</li></ul>");
  }
}