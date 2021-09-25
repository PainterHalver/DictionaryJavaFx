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
    webEngine.loadContent("<h1>promiscuous</h1><h3><i>/promiscuous/</i></h3><h2>tính từ</h2><ul><li>lộn xộn, hỗn tạp, lẫn lộn<ul style=\"list-style-type:circle\"><li>a promiscuous gathering:<i> cuộc tụ tập lộn xộn</i></li><li>a promiscuous crowd:<i> đám đông hỗn tạp</i></li><li>a promiscuous heap of rubbish:<i> đống rác lẫn lộn các thứ</i></li><li>promiscuous bathing:<i> việc tắm chung cả trai lẫn gái</i></li></ul></li><li>bừa bãi, không phân biệt<ul style=\"list-style-type:circle\"><li>promiscuous massacrre:<i> sự tàn sát bừa bãi</i></li><li>promiscuous hospitality:<i> sự tiếp đãi bừa bãi (bạ ai cũng tiếp)</i></li></ul></li><li>chung chạ, bừa bãi, hay ngủ bậy, có tính chất tạp hôn</li><li>(thông tục) tình cờ, bất chợt, ngẫu nhiên, vô tình</li></ul>");
  }
}