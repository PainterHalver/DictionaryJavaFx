package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


public class DictionaryController implements Initializable {
  @FXML
  private Button btnSearch;

  @FXML
  private Button btnGoogleScriptApi;

  @FXML
  private Button btnGgWebEngine;

  @FXML
  private Button btnSpeak;

  @FXML
  private Button btnEdit;

  @FXML
  private Button btnDelete;

  @FXML
  private WebView webView;

  @FXML
  private TextField searchInput;

  @FXML
  private ListView<Expression> wordListView;

  Expression query = new Expression(Constants.INIT_QUERY);

  static class XCell extends ListCell<Expression> {
    HBox hbox = new HBox();
    Label label = new Label("(empty)");
    Pane pane = new Pane();
    Button speakBtn = new Button("\uD83D\uDD0A"); //speaker
    Expression lastItem;

    public XCell() {
      super();
      hbox.getChildren().addAll(label, pane, speakBtn);
      HBox.setHgrow(pane, Priority.ALWAYS);
      speakBtn.cursorProperty().setValue(Cursor.HAND);
      speakBtn.setOnAction(event -> TtsModel.googleTss(lastItem.getExpression()));
    }

    @Override
    protected void updateItem(Expression item, boolean empty) {
      super.updateItem(item, empty);
      setText(null);  // No text in label of super class
      if (empty) {
        lastItem = null;
        setGraphic(null);
      } else {
        lastItem = item;
        label.setText(item!=null ? item.toString() : "aaaa");
        setGraphic(hbox);
      }
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    //Disable Delete button
    btnDelete.setDisable(true);

    WebEngine webEngine = webView.getEngine();
    webEngine.loadContent(DatabaseModel.htmlQuery(query));


    // render word list on app start
    wordListView.setItems(DatabaseModel.allExpressionsQuery(""));
    wordListView.setCellFactory(param -> new XCell());

    // TYPE IN SEARCH INPUT
    searchInput.setOnKeyTyped(keyEvent -> {

      //0. Disable the Delete button
      btnDelete.setDisable(true);

      // 1. Clear list view
      wordListView.getItems().clear();

      // 2. Query words then add to list view
      wordListView.setItems(DatabaseModel.allExpressionsQuery(searchInput.getText()));
    });

    // PICK A WORD IN LISTVIEW EVENT HANDLER
    // https://www.youtube.com/watch?v=Pqfd4hoi5cc
    wordListView.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, s, t1) -> {

          // Persist meaning view
          if(wordListView.getSelectionModel().getSelectedItem() != null) {
            query = wordListView.getSelectionModel().getSelectedItem();
            webEngine.loadContent(DatabaseModel.htmlQuery(query));

            //If selected item is user created, enable the Delete button
            btnDelete.setDisable(!query.isUserCreated());
          }
        });

    // ENTER PRESSED OR SEARCH BUTTON CLICKED
    btnSearch.setOnMouseClicked(mouseEvent -> {
//      query.setExpression(searchInput.getText());
//      if(!Objects.equals(query.getExpression(), "")) {
      if(!Objects.equals(searchInput.getText(), "")) {
        // Not changing the query
        webEngine.loadContent(DatabaseModel.htmlQuery(new Expression(searchInput.getText())));
      }
    });
    searchInput.setOnAction(actionEvent -> {
//      query.setExpression(searchInput.getText());
//      if(!Objects.equals(query.getExpression(), "")) {
      if(!Objects.equals(searchInput.getText(), "")) {
        webEngine.loadContent(DatabaseModel.htmlQuery(new Expression(searchInput.getText())));
      }
    });

    // NON-BLOCKING GOOGLE SCRIPT API CALL
    btnGoogleScriptApi.setOnMouseClicked(mouseEvent -> {
      //loaing text
      webEngine.loadContent(Constants.GOOGLE_API_LOADING_TEXT);

      Thread testThread = new Thread(() -> {
//        query.setExpression(searchInput.getText());
        try {
          String outText = GoogleScriptModel.translate("en", "vi", searchInput.getText());
          Platform.runLater(() -> webEngine.loadContent("<p>" + outText + "</p>")); // p tag for new line if > viewport width
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      testThread.start();
    });
    
    // GOOGLE TRANSLATE WEBENGINE
    btnGgWebEngine.setOnMouseClicked(mouseEvent -> {
//      query.setExpression(searchInput.getText());
      String urlToGo = "https://translate.google.com/?hl=vi&sl=en&tl=vi&text=" + URLEncoder.encode(searchInput.getText(), StandardCharsets.UTF_8) + "&op=translate";
      webEngine.load(urlToGo);
    });

    // TEXT TO SPEAK
    btnSpeak.setOnMouseClicked(mouseEvent -> TtsModel.googleTss(searchInput.getText()));

    //EDIT BUTTON
    btnEdit.setOnMouseClicked(mouseEvent -> {
      FXMLLoader fxmlLoader = new FXMLLoader(DictionaryApplication.class.getResource("word-edit-view.fxml"));
      Scene editScene;
      Stage editStage = new Stage();
      try {
        editScene = new Scene(fxmlLoader.load(), 600, 400);
        editStage.setTitle("Edit");
        editStage.setScene(editScene);
        editStage.show();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    // DELETE BUTTON
    btnDelete.setOnMouseClicked(mouseEvent -> {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Word");
      alert.setHeaderText("Are you sure you want to permanently delete this word?");
      alert.setContentText(query.getExpression());
      Optional<ButtonType> option = alert.showAndWait();

      if (option.isPresent() && option.get() == ButtonType.OK) {
        String word = query.getExpression();

        // 1. Delete the word
        DatabaseModel.deleteExpression(word);
        // 2. Reload the word list view
        wordListView.setItems(DatabaseModel.allExpressionsQuery(searchInput.getText()));
        // 3. Rerender webengine so that user knows word is deleted
        webEngine.loadContent("<h1>Deleted: " + word + "</h1>");
      }
    });
  }

}