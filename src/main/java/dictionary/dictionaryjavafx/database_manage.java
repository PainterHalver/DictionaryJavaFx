package dictionary.dictionaryjavafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.lang.String;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class database_manage {
    static Connection c = null;
    static Statement stmt = null;
    static ResultSet rs = null;

    public static void set_database() {
        try {
            c = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dict_avva.db");
            stmt = c.createStatement();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public static void close_database() throws SQLException {
        if(rs == null) {}
        else {
            rs.close();
            stmt.close();
            c.close();
        }
    }

    public static String search(String r) throws SQLException{
        StringBuilder s= new StringBuilder();
        rs = stmt.executeQuery(String.format("select * from av where word = '%s'", r.toLowerCase()));
        while (rs.next()) {
            String drc = rs.getString("html");
            s.append(drc).append("\n");
            System.out.println("ok");

        }
        if(rs==null) {}
        else {
            rs.close();
        }
        return s.toString();
    }

    public static String[] list_word() throws SQLException {
        rs = stmt.executeQuery("select * from av");
        Set<String> a = new LinkedHashSet<> ();
        while(rs.next()) {
            a.add(rs.getString("word"));
        }
        String[] result = new String [a.size()];
        result = a.toArray(result);
        rs.close();
        return result;
    }

    public static void user_add (String word, String mean) throws SQLException {
        PreparedStatement pstsm = c.prepareStatement("INSERT INTO USER (word, definition) VALUES (?,?)");
        pstsm.setString(1,word);
        pstsm.setString(2,mean);
        pstsm.executeUpdate();
    }

    public static void make_change (String word, String mean) throws SQLException {
        PreparedStatement pstsm = c.prepareStatement("UPDATE USER SET DEFINITION = ? WHERE WORD = ?");
        pstsm.setString(1,mean);
        pstsm.setString(2,word);
        pstsm.execute();
    }

    public static void user_delete (String s) throws SQLException {
        PreparedStatement pstsm = c.prepareStatement("DELETE FROM USER WHERE WORD = ?");
        pstsm.setString(1,s);
        pstsm.executeUpdate();
    }

    public static String user_search(String r) throws SQLException {
        String s="";
            rs = stmt.executeQuery(String.format("select * from user where word = '%s'", r.toLowerCase()));
            while (rs.next()) {
                String drc = rs.getString("definition");
                s = s + drc + "\n";

            }
        if(rs == null) {}
        else
        rs.close();
        return s;
    }

    public static ObservableList<Expression> expressionsQuery(String query) {
        ObservableList<Expression> expressionsRtn = FXCollections.observableArrayList();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dict_avva.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(Constants.QUERY_TIMEOUT);

            // Query from user added table
            ResultSet rs = statement.executeQuery("SELECT DISTINCT(word) FROM ua WHERE word LIKE \"" + query + "%\" ORDER BY LENGTH(word) LIMIT 100");
            while (rs.next()) {
                expressionsRtn.add(new Expression(rs.getString("word"), true));
            }
            // Query from old table
            ResultSet rs1 = statement.executeQuery("SELECT DISTINCT(word) FROM av WHERE word LIKE \"" + query + "%\" ORDER BY LENGTH(word) LIMIT 100");
            while (rs1.next()) {
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

        if (expressionsRtn.size() == 0) {
            expressionsRtn.add(new Expression("NO EXPRESSIONS FOUND FOR THIS QUERY"));
        }

        //list view not on init because you don't want user added expressions to always stay on top
        if(!Objects.equals(query, "")) {
            Comparator<Expression> expressionComparator = Comparator.comparing(Expression::getExpression);
            expressionsRtn.sort(expressionComparator);
        }

        return expressionsRtn;
    }

    public static String htmlQuery(Expression expression) {
        StringBuilder html = new StringBuilder();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:./src/main/database/dict_avva.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(Constants.QUERY_TIMEOUT);

            String exp = expression.getExpression();

            // statement for filtering stupid padding that cannot be trimmed with TRIM(word)
            ResultSet rs = statement.executeQuery(
                "SELECT html FROM "
                    + (expression.isUserCreated() ? "user" : "av")
                    + " WHERE substr(word, -length('" + exp + "')) = \""
                    + exp
                    + "\" and substr(word, -length('" + exp + "') - 1, 1) NOT GLOB '[A-Za-z1-9. -]'");

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

    public static void addExpression(String expression, String pronunciation, String meaning) throws SQLException {
        set_database();
        PreparedStatement pstsm = c.prepareStatement("INSERT INTO USER (word, html, created, last_modified) VALUES (?,?,?,?)");
        pstsm.setString(1,expression);
        String markup =
            "<h1>"
            + expression+ "</h1><br><p>/"
            + pronunciation + "/</p><br><p>"
            + meaning + "</p>";
        pstsm.setString(2,markup);
        long unixTime = System.currentTimeMillis();
        pstsm.setLong(3, unixTime);
        pstsm.setLong(4, unixTime);
        pstsm.executeUpdate();
        close_database();
    }

    public static void main(String[] args) throws SQLException {
//        addExpression("test unix", "","kiem tra unix");
        set_database();
        close_database();
        Date time= new Date(1633321452175L);
        System.out.println(time);
    }
}

