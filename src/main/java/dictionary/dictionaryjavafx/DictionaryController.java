package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class DictionaryController implements Initializable {
  @FXML
  private Button btnSearch;
  
  @FXML
  private Button btnGoogleScriptApi;

  @FXML
  private Button btnGgWebEngine;

  @FXML
  private WebView webView;

  @FXML
  private TextField searchInput;

  @FXML
  private ListView<Expression> wordListView;

  Expression query = new Expression(Constants.INIT_QUERY);
//  String outText = "";

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    WebEngine webEngine = webView.getEngine();
    webEngine.loadContent(DatabaseModel.htmlQuery(query));


    // render word list on app start
    wordListView.setItems(DatabaseModel.expressionsQuery(""));

    // TYPE IN SEARCH INPUT
    searchInput.setOnKeyTyped(keyEvent -> {

      // 1. Clear list view
      wordListView.getItems().clear();

      // 2. Query words then add to list view
      wordListView.setItems(DatabaseModel.expressionsQuery(searchInput.getText()));
    });

    // PICK A WORD IN LISTVIEW EVENT HANDLER
    // https://www.youtube.com/watch?v=Pqfd4hoi5cc
    wordListView.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, s, t1) -> {

          // Persist meaning view
          if(wordListView.getSelectionModel().getSelectedItem() != null) {
            query = wordListView.getSelectionModel().getSelectedItem();
            webEngine.loadContent(DatabaseModel.htmlQuery(query));
          }
        });

    // ENTER PRESSED OR SEARCH BUTTON CLICKED
    btnSearch.setOnMouseClicked(mouseEvent -> {
      query.setExpression(searchInput.getText());
      if(!Objects.equals(query.getExpression(), "")) {
        webEngine.loadContent(DatabaseModel.htmlQuery(query));
      }
    });
    searchInput.setOnAction(actionEvent -> {
      query.setExpression(searchInput.getText());
      if(!Objects.equals(query.getExpression(), "")) {
        webEngine.loadContent(DatabaseModel.htmlQuery(query));
      }
    });

    // NON-BLOCKING GOOGLE SCRIPT API CALL
    btnGoogleScriptApi.setOnMouseClicked(mouseEvent -> {
      //loaing text
      webEngine.loadContent(Constants.GOOGLE_API_LOADING_TEXT);

      Thread testThread = new Thread(() -> {
        query.setExpression(searchInput.getText());
        try {
          String outText = GoogleScriptModel.translate("en", "vi", query.getExpression());
          Platform.runLater(() -> webEngine.loadContent("<p>" + outText + "</p>")); // p tag for new line if > viewport width
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      testThread.start();
    });
    
    // GOOGLE TRANSLATE WEBENGINE
    btnGgWebEngine.setOnMouseClicked(mouseEvent -> {
      query.setExpression(searchInput.getText());
      String urlToGo = "https://translate.google.com/?hl=vi&sl=en&tl=vi&text=" + URLEncoder.encode(query.getExpression(), StandardCharsets.UTF_8) + "&op=translate";
      webEngine.load(urlToGo);
    });
  }
}