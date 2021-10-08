package dictionary.dictionaryjavafx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

  private Expression currentExpression = null;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

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
          }
        });
  }
}
