package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import javax.swing.text.html.ImageView;


public class DictionaryController implements Initializable {
  @FXML
  private ComboBox filterComboBox;

  @FXML
  private ComboBox ttsComboBox;

  @FXML
  private Button btnSearch;

  @FXML
  private Button btnGoogleScriptApi;

  @FXML
  private Button btnGgWebEngine;

  @FXML
  private Button btnEdit;

  @FXML
  private Button btnDelete;

  @FXML
  private WebView webView;

  @FXML
  private TextField searchInput;

  @FXML
  private Button btnSpeaker;

  @FXML
  private ListView<Expression> wordListView;


  Expression query = new Expression(Constants.INIT_QUERY);
  String currentListType = "All";
  String currentTtsType = "FreeTTS (Offline)";


  private void setList(String currentListType) {
    wordListView.setCellFactory(param -> new XCell());
    switch (currentListType) {
      case "All" -> wordListView.setItems(DatabaseModel.allExpressionsQuery(searchInput.getText()));
      case "User Created" -> wordListView.setItems(
          DatabaseModel.userExpressionsQuery(searchInput.getText()));
      case "Favorite" -> wordListView.setItems(
          DatabaseModel.favoriteExpressionsQuery(searchInput.getText()));
    }
  }

  private void renderWebView(Expression query) {
    WebEngine engine = webView.getEngine();
    String markup = DatabaseModel.htmlQuery(query);
    engine.loadContent(markup);
    if (Objects.equals(markup, Constants.NO_EXPRESSIONS_FOUND)) {
      if (!btnSpeaker.getStyleClass().contains("hidden")){
        btnSpeaker.getStyleClass().add("hidden");
      }
    } else {
      btnSpeaker.getStyleClass().remove("hidden");
    }
  }

  class XCell extends ListCell<Expression> {
    HBox hbox = new HBox();
    Label label = new Label("(empty)");
    Pane pane = new Pane();
    Button userBtn = new Button("????");
    ToggleButton favBtn = new ToggleButton("???");
    Expression lastItem;

    public XCell() {
      super();
      hbox.getChildren().addAll(label, pane);
      HBox.setHgrow(pane, Priority.ALWAYS);
      hbox.setAlignment(Pos.CENTER);
      favBtn.getStyleClass().add("btn-fav");
      userBtn.getStyleClass().add("btn-user");
      userBtn.setDisable(true);
      favBtn.setOnAction(event -> {
        if (favBtn.isSelected()) {
          DatabaseModel.addFavourite(lastItem.getId());
        } else if (!favBtn.isSelected()) {
          DatabaseModel.deleteFavourite(lastItem.getId());
        }
        setList(currentListType);
      });
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
        label.setText(item!=null ? item.toString() : "");
        assert item != null;
        if (item.isUserCreated()) {
          hbox.getChildren().clear();
          hbox.getChildren().addAll(label, pane, userBtn);
        } else if (item.isFavourite()) {
          hbox.getChildren().clear();
          favBtn.setSelected(true);
          hbox.getChildren().addAll(label, pane, favBtn);
        } else {
          hbox.getChildren().clear();
          favBtn.setSelected(false);
          hbox.getChildren().addAll(label, pane,favBtn);
        }
        setGraphic(hbox);
      }
    }
  }

  public void speak(Expression query) {
    switch (currentTtsType) {
      case "FreeTTS (Offline)" -> TtsModel.freeTts(query.getExpression());
      case "GoogleTTS (API)" -> TtsModel.apiTTS(query.getExpression(), Constants.GOOGLE_ENG_TTS_URL);
      case "VoicersTTS (API)" -> TtsModel.apiTTS(query.getExpression(), Constants.VOICERSS_TTS_URL);
    }
  }

  public class Bridge {
    public void say() {
      speak(query);
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    btnSpeaker.setOnAction(event -> speak(query));

    //Disable Delete button
    btnDelete.setDisable(true);

    WebEngine webEngine = webView.getEngine();
    webEngine.setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("html.css")).toString());
    renderWebView(query);


    // render word list on app start
    wordListView.setItems(DatabaseModel.allExpressionsQuery(""));
    wordListView.setCellFactory(param -> new XCell());

    // INIT SORT FILTER BOX
    ObservableList<String> filterOptions = FXCollections.observableArrayList(
        "All",
        "User Created",
        "Favorite"
    );
    filterComboBox.getItems().addAll(filterOptions);
    filterComboBox.getSelectionModel().select(currentListType);
    filterComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
      currentListType = (String) filterComboBox.getSelectionModel().getSelectedItem();
      setList(currentListType);
    });

    //INIT TTS COMBO BOX
    ObservableList<String> ttsOptions = FXCollections.observableArrayList(
            "FreeTTS (Offline)",
            "GoogleTTS (API)",
            "VoicersTTS (API)"
    );
    ttsComboBox.getItems().addAll(ttsOptions);
    ttsComboBox.getSelectionModel().select(currentTtsType);
    ttsComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
      currentTtsType = (String) ttsComboBox.getSelectionModel().getSelectedItem();
    });

    // TYPE IN SEARCH INPUT
    searchInput.setOnKeyTyped(keyEvent -> {

      //0. Disable the Delete button
      btnDelete.setDisable(true);

      // 1. Clear list view
      wordListView.getItems().clear();

      // 2. Query words then add to list view
      setList(currentListType);
    });

    // PICK A WORD IN LISTVIEW EVENT HANDLER
    // https://www.youtube.com/watch?v=Pqfd4hoi5cc
    wordListView.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, s, t1) -> {

          // Persist meaning view
          if(wordListView.getSelectionModel().getSelectedItem() != null) {
            query = wordListView.getSelectionModel().getSelectedItem();
            renderWebView(query);

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
        renderWebView(new Expression(searchInput.getText()));
      }
    });
    searchInput.setOnAction(actionEvent -> {
//      query.setExpression(searchInput.getText());
//      if(!Objects.equals(query.getExpression(), "")) {
      if(!Objects.equals(searchInput.getText(), "")) {
        renderWebView(new Expression(searchInput.getText()));
      }
    });

    // NON-BLOCKING GOOGLE SCRIPT API CALL
    btnGoogleScriptApi.setOnMouseClicked(mouseEvent -> {
      FXMLLoader fxmlLoader = new FXMLLoader(DictionaryApplication.class.getResource("translate-view.fxml"));
      Scene translateScene;
      Stage translateStage = new Stage();
      ExpressionHolder holder  = ExpressionHolder.getInstance();
      holder.setExpression(new Expression(searchInput.getText()));
      try {
        translateScene = new Scene(fxmlLoader.load(), 350, 400);
        translateStage.setTitle("Google Script API");
        translateStage.setMinWidth(300);
        translateStage.setMinHeight(350);
        translateStage.setScene(translateScene);
        translateStage.initModality(Modality.APPLICATION_MODAL);
        translateStage.initOwner(btnGoogleScriptApi.getScene().getWindow());
        translateStage.showAndWait();
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        setList(currentListType);
      }
    });
    
    // GOOGLE TRANSLATE WEBENGINE
//    btnGgWebEngine.setOnMouseClicked(mouseEvent -> {
////      query.setExpression(searchInput.getText());
//      String urlToGo = "https://translate.google.com/?hl=vi&sl=en&tl=vi&text=" + URLEncoder.encode(searchInput.getText(), StandardCharsets.UTF_8) + "&op=translate";
//      webEngine.load(urlToGo);
//    btnSpeaker.getStyleClass().remove("hidden");
//    });

    //EDIT BUTTON
    btnEdit.setOnMouseClicked(mouseEvent -> {
      FXMLLoader fxmlLoader = new FXMLLoader(DictionaryApplication.class.getResource("word-edit-view.fxml"));
      Scene editScene;
      Stage editStage = new Stage();

      // Send current Expression to Edit view
      ExpressionHolder holder  = ExpressionHolder.getInstance();
      holder.setExpression(query);

      try {
        editScene = new Scene(fxmlLoader.load(), 600, 400);
        editStage.setTitle("Edit");
        editStage.setScene(editScene);
        editStage.setMinWidth(600);
        editStage.setMinHeight(400);

        // Block main window when edit view is openned
        // https://stackoverflow.com/questions/46612307/how-to-block-a-main-window-in-javafx
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.initOwner(btnEdit.getScene().getWindow());
        editStage.showAndWait();

      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        // Rerender word list view after editting
        setList(currentListType);
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
        setList(currentListType);
        // 3. Rerender webengine so that user knows word is deleted
        webEngine.loadContent("<h1>Deleted: " + word + "</h1>");
        btnSpeaker.getStyleClass().remove("hidden");
        // 4. Set current exp back to ''
        query = new Expression("");
      }
    });
  }

}