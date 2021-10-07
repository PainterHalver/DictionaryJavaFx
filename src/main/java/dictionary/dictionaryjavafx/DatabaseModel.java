package dictionary.dictionaryjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

  public static ObservableList<Expression> expressionsQuery(String query, String ... args) {
    ObservableList<Expression> expressionsRtn = FXCollections.observableArrayList();
    try {
      openConnection();
      // Query from user added table
      ResultSet rs = statement.executeQuery("SELECT DISTINCT(word) FROM ua WHERE word LIKE \"" + query + "%\" ORDER BY LENGTH(word) LIMIT 100");
      while (rs.next()) {
        expressionsRtn.add(new Expression(rs.getString("word"), true));
      }
      if(args.length == 0) {
        // Query from old table
        ResultSet rs1 = statement.executeQuery(
            "SELECT DISTINCT(word) FROM av WHERE word LIKE \"" + query +
                "%\" ORDER BY LENGTH(word) LIMIT 100");
        while (rs1.next()) {
          expressionsRtn.add(new Expression(rs.getString("word")));
        }
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
      Comparator<Expression> expressionComparator = Comparator.comparing(Expression::getExpression);
      expressionsRtn.sort(expressionComparator);
    }

    return expressionsRtn;
  }

  public static String htmlQuery(Expression expression) {
    StringBuilder html = new StringBuilder();
    try {
      openConnection();
      String exp = expression.getExpression();

      // statement for filtering stupid padding that cannot be trimmed with TRIM(word)
      ResultSet rs = statement.executeQuery(
          "SELECT html FROM "
              + (expression.isUserCreated() ? "ua" : "av")
              + " WHERE substr(word, -length('" + exp + "')) = \""
              + exp
              + "\" and substr(word, -length('" + exp + "') - 1, 1) NOT GLOB '[A-Za-z1-9. -]'");

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
        + expression+ "</h1><h3>/"
        + pronunciation + "/</h3><p>"
        + meaning + "</p>";
  }

  public static void addExpression(String expression, String pronunciation, String meaning) {
    try {
      openConnection();
      expression = expression.toLowerCase(Locale.ROOT);
      PreparedStatement pstm = connection.prepareStatement("INSERT INTO ua (word, meaning, pronunciation, html, created_at, last_modified) VALUES (?,?,?,?,?,?)");
      pstm.setString(1,expression);
      pstm.setString(2,meaning);
      pstm.setString(3,pronunciation);

      String markup = generateMarkup(expression, pronunciation, meaning);

      pstm.setString(4,markup);
      long unixTime = System.currentTimeMillis();
      pstm.setLong(5, unixTime);
      pstm.setLong(6, unixTime);
      pstm.executeUpdate();

    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }
  }

  public static void deleteExpression(String expression) {
    try {
      openConnection();
      statement.executeUpdate("DELETE FROM ua WHERE WORD = '" + expression + "'");
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    } finally {
      closeConnection();
    }
  }

  public static void editExpression(String oldExpression, String newExpression, String pronunciation, String meaning) {
    try {
      openConnection();
      newExpression = newExpression.toLowerCase(Locale.ROOT);
      PreparedStatement pstm = connection.prepareStatement(
          "UPDATE ua SET word = ?, meaning = ?, pronunciation = ?, html = ?, last_modified = ? WHERE WORD = ?"
      );
      pstm.setString(1, newExpression);
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

  public static void main(String[] args) {
    addExpression("test5", "", "xin chào thế giới");
    addExpression("test2", "", "xin chào thế giới");
    addExpression("test3", "", "xin chào thế giới");
    addExpression("test4", "", "xin chào thế giới");
//    deleteExpression("test1");
  }
}
