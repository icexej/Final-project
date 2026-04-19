import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SetupDatabase {
    private static final String URL = "jdbc:sqlite:social_media.db";

    public static void createNewDatabase() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "role TEXT);";

        String sqlPosts = "CREATE TABLE IF NOT EXISTS posts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "content TEXT, " +
                "platform TEXT, " +
                "author TEXT, " +
                "date TEXT, " +
                "type TEXT);";

        String sqlComments = "CREATE TABLE IF NOT EXISTS comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "post_id INTEGER, " +
                "author TEXT, " +
                "text TEXT, " +
                "FOREIGN KEY(post_id) REFERENCES posts(id));";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsers);
            stmt.execute(sqlPosts);
            stmt.execute(sqlComments);

            System.out.println("База данных обновлена! Таблица комментариев готова.");

        } catch (SQLException e) {
            System.out.println("Ошибка при создании базы: " + e.getMessage());
        }
    }
}