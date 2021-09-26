package dictionary.dictionaryjavafx;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.controlsfx.control.textfield.TextFields;

public class DictionaryController implements Initializable {
  @FXML
  private Button btnTest;

  @FXML
  private Button btnGgWebEngine;

  @FXML
  private WebView webView;

  @FXML
  private TextField searchInput;

  @FXML
  private ListView<String> wordListView;

  String currentWord = "hello";

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    WebEngine webEngine = webView.getEngine();
    webEngine.loadContent(DatabaseModel.htmlQuery(currentWord));


    // render word list on app start
    wordListView.getItems().addAll(DatabaseModel.wordsQuery(""));
    // TYPE IN SEARCH INPUT
    searchInput.setOnKeyTyped(keyEvent -> {
      System.out.println("keyEvent!!!!");

      // 1. Clear list view
      wordListView.getItems().clear();

      // 2. Query words then add to list view
      String[] wordSuggestions = DatabaseModel.wordsQuery(searchInput.getText());
      wordListView.getItems().addAll(wordSuggestions);
    });

    // PICK A WORD IN LISTVIEW EVENT HANDLER
    wordListView.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observableValue, String s,
                              String t1) {
            currentWord = wordListView.getSelectionModel().getSelectedItem();

            // Persist meaning view
            if(currentWord != null) {
              webEngine.loadContent(DatabaseModel.htmlQuery(currentWord));
            }

          }
        });

    // GOOGLE TRANSLATE WEBENGINE
    btnGgWebEngine.setOnMouseClicked(mouseEvent -> {
      String query = searchInput.getText();
      String urlToGo = "https://translate.google.com/?hl=vi&sl=en&tl=vi&text=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&op=translate";
      webEngine.load(urlToGo);
    });
  }
}