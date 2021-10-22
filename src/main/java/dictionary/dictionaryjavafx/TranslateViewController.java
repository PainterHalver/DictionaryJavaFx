package dictionary.dictionaryjavafx;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

public class TranslateViewController implements Initializable {

  @FXML
  private TextArea textInput;

  @FXML
  private TextArea textOutput;

  @FXML
  private Button btnTranslate;

  @FXML
  private Button btnSpeakEng;

  @FXML
  private Button btnSpeakVie;

  private void translate() {
    textOutput.setText(Constants.GOOGLE_API_LOADING_TEXT);
    Thread testThread = new Thread(() -> {
      try {
        String outText = GoogleScriptModel.translate("en", "vi", textInput.getText());
        Platform.runLater(() -> textOutput.setText(outText));
      } catch (IOException e) {
        Platform.runLater(() -> {
          textOutput.setText(Constants.NO_INTERNET);
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Error");
          alert.setHeaderText("");
          alert.setContentText(Constants.NO_INTERNET);
          Optional<ButtonType> option = alert.showAndWait();
        });
      }
    });
    testThread.start();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    textOutput.setEditable(false);

    ExpressionHolder holder = ExpressionHolder.getInstance();
    Expression exp = holder.getExpression();
    if(!Objects.equals(exp.getExpression(), "")) {
      textInput.setText(exp.getExpression());
      translate();
    }

    btnTranslate.setOnMouseClicked(mouseEvent -> {translate();});

    btnSpeakEng.setOnMouseClicked(mouseEvent -> {
      TtsModel.apiTTS(textInput.getText(), Constants.GOOGLE_ENG_TTS_URL);
    });

    btnSpeakVie.setOnMouseClicked(mouseEvent -> {
      TtsModel.apiTTS(textOutput.getText(), Constants.GOOGLE_VIE_TTS_URL);
    });
  }
}
