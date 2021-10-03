package dictionary.dictionaryjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * https://github.com/xerial/sqlite-jdbc
 */
public class DatabaseModel {

  public static ObservableList<Expression> expressionsQuery(String query) {
    ObservableList<Expression> expressionsRtn = FXCollections.observableArrayList();
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dictionaryData.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.
      ResultSet rs = statement.executeQuery("SELECT DISTINCT(word) FROM av WHERE word LIKE \"" + query + "%\" LIMIT 100");


      while (rs.next()) {
        expressionsRtn.add(new Expression(rs.getString("word")));
      }
    } catch (SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
    } finally {
      try {
        if (connection != null) connection.close();
      } catch (SQLException e) {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }
    return expressionsRtn;
  }

  @Deprecated
  public static String[] wordsQuery(String query) {
    ArrayList<String> wordsRtn = new ArrayList<>();
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dictionaryData.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.
      ResultSet rs = statement.executeQuery("SELECT DISTINCT(word) FROM av WHERE word LIKE \"" + query + "%\" LIMIT 100");


      while (rs.next()) {
        wordsRtn.add(rs.getString("word"));
      }
    } catch (SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
    } finally {
      try {
        if (connection != null) connection.close();
      } catch (SQLException e) {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }
    return wordsRtn.toArray(new String[0]);
  }

  public static String htmlQuery(String query) {
    StringBuilder html = new StringBuilder();
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dictionaryData.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);
      ResultSet rs = statement.executeQuery("SELECT * FROM av WHERE word = \"" + query + "\"");

      while (rs.next()) {
        html.append(rs.getString("html"));
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      try {
        if (connection != null) connection.close();
      } catch (SQLException e) {
        System.err.println(e.getMessage());
      }
    }
    return html.toString().equals("")
        ? "<h3>Sorry, no words found! <br> Maybe try using Google Translate instead?</h3>"
        : html.toString();
  }
}
