package dictionary.dictionaryjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * https://github.com/xerial/sqlite-jdbc
 */
public class DatabaseModel {
  private static Connection connection = null;
  private static Statement statement = null;

  private static void openConnection() {
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dictionaryData.db");
      statement = connection.createStatement();
      statement.setQueryTimeout(Constants.QUERY_TIMEOUT);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static void closeConnection() {
    try {
      if (connection != null) connection.close();
      if (statement != null) statement.close();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  public static ObservableList<Expression> userExpressionsQuery(String query) {
    ObservableList<Expression> expressionsRtn = FXCollections.observableArrayList();
    try {
      openConnection();
//      ResultSet rs = statement.executeQuery("SELECT * FROM ua WHERE word LIKE \"" + query + "%\" ORDER BY word");
      PreparedStatement pstm = connection.prepareStatement("SELECT * FROM ua WHERE word LIKE ? ORDER BY word");
      pstm.setString(1, query + "%");
      ResultSet rs = pstm.executeQuery();
      while (rs.next()) {
        expressionsRtn.add(new Expression(rs.getString("word"), rs.getString("pronunciation"),
            rs.getString("meaning"), true, rs.getLong("created_at"), rs.getLong("last_modified")));
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }
    return expressionsRtn;
  }

  public static ObservableList<Expression> allExpressionsQuery(String query) {
    ObservableList<Expression> expressionsRtn = FXCollections.observableArrayList();
    // Query from user added table
    expressionsRtn.addAll(userExpressionsQuery(query));
    try {
      openConnection();
      // Query from old table
//      ResultSet rs = statement.executeQuery(
//          "SELECT DISTINCT(word) FROM av WHERE word LIKE \"" + query +
//              "%\" ORDER BY LENGTH(word) LIMIT 100");
      PreparedStatement pstm = connection.prepareStatement("SELECT DISTINCT(word) FROM av WHERE word LIKE ? ORDER BY id, word LIMIT 100");
      pstm.setString(1, query + "%");
      ResultSet rs = pstm.executeQuery();
      while (rs.next()) {
        expressionsRtn.add(new Expression(rs.getString("word")));
      }
    } catch (SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }

//    if (expressionsRtn.size() == 0) {
//      expressionsRtn.add(new Expression("NO EXPRESSIONS FOUND FOR THIS QUERY"));
//    }

    //list view not on init because you don't want user added expressions to always stay on top
    if(!Objects.equals(query, "")) {
      Collections.sort(expressionsRtn, new Comparator<Expression>() {
        @Override
        public int compare(Expression left, Expression right) {
          String leftExp = left.getExpression();
          String rightExp = right.getExpression();
          if(leftExp.length() < rightExp.length()){
            return -1;
          }
          return left.getExpression().compareTo(right.getExpression());
        }
      });
    }

    return expressionsRtn;
  }

  public static String htmlQuery(Expression expression) {
    StringBuilder html = new StringBuilder();
    try {
      openConnection();
      String exp = expression.getExpression();

//       statement for filtering stupid padding that cannot be trimmed with TRIM(word)
//      ResultSet rs = statement.executeQuery(
//          "SELECT html FROM "
//              + (expression.isUserCreated() ? "ua" : "av")
//              + " WHERE substr(word, -length('" + exp + "')) = \""
//              + exp
//              + "\" and substr(word, -length(\"" + exp + "\") - 1, 1) NOT GLOB '[A-Za-z1-9. -]'");

      PreparedStatement pstm = connection.prepareStatement("SELECT html FROM \""
          + (expression.isUserCreated() ? "ua" : "av") + "\" WHERE substr(word, -length(?)) =  ?  and substr(word, -length(?) - 1, 1) NOT GLOB '[A-Za-z1-9. -]'");
      pstm.setString(1, exp);
      pstm.setString(2, exp);
      pstm.setString(3, exp);
      ResultSet rs = pstm.executeQuery();
      while (rs.next()) {
        html.append(rs.getString("html"));
      }

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }

    return html.toString().equals("")
        ? Constants.NO_EXPRESSIONS_FOUND
        : html.toString();
  }

  public static String generateMarkup(String expression, String pronunciation, String meaning) {
    return "<h1>"
        + expression+ "</h1><h3>/<i>"
        + pronunciation + "</i>/</h3><p>"
        + meaning + "</p>";
  }

  public static boolean inUserDatabase(String s) {
    ObservableList<Expression> userData = userExpressionsQuery(s);
    for(Expression i : userData) {
      if (Objects.equals(i.getExpression(), s)) {
        return true;
      }
    }
    return false;
  }

  /**
   * throw Exception de button Add tu xu ly.
   */
  public static void addExpression(String expression, String pronunciation, String meaning)
      throws SQLException {
    if (Objects.equals(expression, "")) {
      throw new SQLException("Expression can not be empty!");
    } else if (inUserDatabase(expression.trim())) {
      throw new SQLException("Expression already exists in user database (duplicated)!");
    }
    try {
      openConnection();
      PreparedStatement pstm = connection.prepareStatement("INSERT INTO ua (word, meaning, pronunciation, html, created_at, last_modified) VALUES (?,?,?,?,?,?)");
      pstm.setString(1,expression.toLowerCase(Locale.ROOT).trim());
      pstm.setString(2,meaning);
      pstm.setString(3,pronunciation);

      String markup = generateMarkup(expression, pronunciation, meaning);

      pstm.setString(4,markup);
      long unixTime = System.currentTimeMillis();
      pstm.setLong(5, unixTime);
      pstm.setLong(6, unixTime);
      pstm.executeUpdate();

    } finally {
      closeConnection();
    }
  }

  public static void deleteExpression(String expression) {
    try {
      openConnection();
//      statement.executeUpdate("DELETE FROM ua WHERE WORD = '" + expression + "'");
      PreparedStatement pstm = connection.prepareStatement("DELETE FROM ua WHERE WORD = ?");
      pstm.setString(1, expression);
      pstm.executeUpdate();
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }
  }

  public static void updateExpression(String oldExpression, String newExpression, String pronunciation, String meaning) throws SQLException {
    try {
      openConnection();
      PreparedStatement pstm = connection.prepareStatement(
          "UPDATE ua SET word = ?, meaning = ?, pronunciation = ?, html = ?, last_modified = ? WHERE WORD = ?"
      );
      pstm.setString(1, newExpression.toLowerCase(Locale.ROOT).trim());
      pstm.setString(2, meaning);
      pstm.setString(3, pronunciation);

      String markup = generateMarkup(newExpression, pronunciation, meaning);

      pstm.setString(4, markup);
      pstm.setLong(5, System.currentTimeMillis());
      pstm.setString(6, oldExpression);

      pstm.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }
  }
}
