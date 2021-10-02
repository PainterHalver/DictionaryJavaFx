module dictionary.dictionaryjavafx {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires org.kordamp.bootstrapfx.core;
  requires java.sql;
  requires java.desktop;

  opens dictionary.dictionaryjavafx to javafx.fxml;
  exports dictionary.dictionaryjavafx;
}