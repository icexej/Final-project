import java.io.Serializable; // Не забудь импорт!

public class User implements Serializable {

    private String username;
    private String password;
    private String role; // "ADMIN" или "USER"

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }

    public String getPassword() { return password; }
}