package dictionary.dictionaryjavafx;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WordEditController implements Initializable {

  @FXML
  private TextField searchInput;

  @FXML
  private ListView userListView;

  @FXML
  private ComboBox sortComboBox;

  @FXML
  private TextField wordInput;

  @FXML
  private TextField pronunciationInput;

  @FXML
  private TextArea meaningInput;

  @FXML
  private WebView previewWebview;

  @FXML
  private Button btnAdd;

  @FXML
  private Button btnDelete;

  @FXML
  private Button btnUpdate;

  @FXML
  private Button btnClose;

  private Expression currentExpression = null;

  private void clearInput() {
    wordInput.setText("");
    pronunciationInput.setText("");
    meaningInput.setText("");
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    // THREAD btn DELETE
    btnDelete.setDisable(true);


    WebEngine previewWebEngine = previewWebview.getEngine();

    // INIT SORT COMBOBOX
    ObservableList<String> sortOptions = FXCollections.observableArrayList(
            "Name",
            "Created Date",
            "Last Modified"
        );
    sortComboBox.getItems().addAll(sortOptions);
    sortComboBox.getSelectionModel().selectFirst();

    // INIT USER LIST VIEW
    userListView.setItems(DatabaseModel.userExpressionsQuery(""));

    // TYPE IN SEARCH INPUT
    searchInput.setOnKeyTyped(keyEvent -> {
      //0. Disable the Delete button
      btnDelete.setDisable(true);

      // 1. Clear list view
      userListView.getItems().clear();

      // 2. Query words then add to list view
      userListView.setItems(DatabaseModel.userExpressionsQuery(searchInput.getText()));
    });

    // PICK A WORD IN LISTVIEW EVENT HANDLER
    userListView.getSelectionModel().selectedItemProperty().addListener(
        (observableValue, s, t1) -> {

          // Persist meaning view
          if(userListView.getSelectionModel().getSelectedItem() != null) {

            currentExpression = (Expression) userListView.getSelectionModel().getSelectedItem();
            // load expression to web engine and input fields
            previewWebEngine.loadContent(DatabaseModel.htmlQuery(currentExpression));
            wordInput.setText(currentExpression.getExpression());
            pronunciationInput.setText(currentExpression.getPronunciation());
            meaningInput.setText(currentExpression.getMeaning());

            btnDelete.setDisable(!currentExpression.isUserCreated());
          }
        });

    // RENDER PREVIEW WHEN INPUT CHANGE
    ArrayList<TextField> textFields = new ArrayList<>();
    textFields.add(wordInput);
    textFields.add(pronunciationInput);
    for(TextField tf : textFields) {
      tf.setOnKeyTyped(keyEvent -> {
        previewWebEngine.loadContent(DatabaseModel.generateMarkup(wordInput.getText(),pronunciationInput.getText(),meaningInput.getText()));
      });
    }
    meaningInput.setOnKeyTyped(keyEvent -> {
      previewWebEngine.loadContent(DatabaseModel.generateMarkup(wordInput.getText(),pronunciationInput.getText(),meaningInput.getText()));
    });

    // ADD BUTTON CLICKED
    btnAdd.setOnMouseClicked(mouseEvent -> {
      String word = wordInput.getText();
      try {
      // 1. Add to database
        DatabaseModel.addExpression(word,pronunciationInput.getText(),meaningInput.getText());
      // 2. Rerender userListView
        userListView.setItems(DatabaseModel.userExpressionsQuery(searchInput.getText()));
      // 3. Rerender webengine so that user knows word is ADDED
        previewWebEngine.loadContent("<h3>ADDED: " + word + "</h3>");
      // 4. Clear input fields
        clearInput();
      } catch (SQLException e) {
        // code 19: duplicated word
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("");
        alert.setContentText(e.getMessage());
        Optional<ButtonType> option = alert.showAndWait();
      }
    });

    // DELETE BUTTON
    btnDelete.setOnMouseClicked(mouseEvent -> {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Delete Word");
      alert.setHeaderText("Are you sure you want to permanently delete this word?");
      alert.setContentText(currentExpression.getExpression());
      Optional<ButtonType> option = alert.showAndWait();

      if (option.isPresent() && option.get() == ButtonType.OK) {
        String word = currentExpression.getExpression();
        // 1. Delete the word
        DatabaseModel.deleteExpression(word);
        // 2. Reload the word list view
        userListView.setItems(DatabaseModel.userExpressionsQuery(searchInput.getText()));
        // 3. Rerender webengine so that user knows word is deleted
        previewWebEngine.loadContent("<h3>Deleted: " + word + "</h3>");
        // 4. Clear input fields
        clearInput();
      }
    });

    // UPDATE BUTTON CLICKED
    btnUpdate.setOnMouseClicked(mouseEvent -> {
        String oldExpression = currentExpression.getExpression();
        try {
          // 1. Update to database
          DatabaseModel.updateExpression(oldExpression, wordInput.getText(), pronunciationInput.getText(), meaningInput.getText());
          // 2. Rerender userListView
          userListView.setItems(DatabaseModel.userExpressionsQuery(searchInput.getText()));
          // 3. Rerender webengine so that user knows word is ADDED
          previewWebEngine.loadContent("<h3>UPDATED</h3>");
        } catch (SQLException e) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("");
          alert.setContentText(e.getMessage());
          Optional<ButtonType> option = alert.showAndWait();
        }
    });

    // CLOSE BUTTON CLICKED
    btnClose.setOnMouseClicked(mouseEvent -> {
      Stage stage = (Stage) btnClose.getScene().getWindow();
      stage.close();
    });
  }
}
