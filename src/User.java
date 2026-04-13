public class User {
    private String username;
    private String password;
    private String role; // "ADMIN" или "USER"

    // Конструктор
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Геттеры (нужны для проверки входа и сохранения в файл)
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Сеттеры (если вдруг захочешь сменить пароль, но пока не обязательно)
    public void setPassword(String password) {
        this.password = password;
    }
}