package dictionary.dictionaryjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseModel {


  public static void main(String[] args) {
    System.out.println("Hello Database!");

    long begin = System.currentTimeMillis();
    System.out.println(begin);
    Connection connection = null;
    try {
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dictionaryData.db");

      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.

      ResultSet rs = statement.executeQuery("SELECT * FROM av WHERE word LIKE \"abso%\" LIMIT 100");

      while(rs.next())
      {
        // read the result set
        System.out.print("word:" + rs.getString("word"));
        System.out.print("meaning: " + rs.getString(4) + "\n");
      }
      System.out.println("Time elapsed: " + (System.currentTimeMillis() - begin));


    } catch (SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
    } finally {
      try {
        if(connection != null) connection.close();
      } catch(SQLException e) {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }

  }
}
