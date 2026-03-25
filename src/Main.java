import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PostManager manager = new PostManager();
        manager.loadFromFile();
        Scanner scanner = new Scanner(System.in);

        User currentUser = null;

        System.out.println("=== СИСТЕМА ПЛАНИРОВАНИЯ ПОСТОВ ===");

        while (currentUser == null) {
            System.out.println("\nВыберите тип входа:");
            System.out.println("1. Администратор (Полный доступ)");
            System.out.println("2. Гость (Только просмотр)");
            System.out.print("Ваш выбор: ");

            String loginChoice = scanner.nextLine();

            if (loginChoice.equals("1")) {
                // Вход для Админа
                System.out.print("Введите пароль для Админа: ");
                String passIn = scanner.nextLine();

                if (passIn.equals("2404")) {
                    currentUser = new User("Admin", passIn, "ADMIN");
                    System.out.println("Успешный вход! Права администратора получены.");
                } else {
                    System.out.println("Ошибка: Неверный пароль!");
                }

            } else if (loginChoice.equals("2")) {
                // Вход для Гостя (без пароля)
                currentUser = new User("Guest", "", "USER");
                System.out.println("Вы вошли как Гость. Доступен только просмотр.");

            } else {
                System.out.println("Ошибка: Выберите 1 или 2.");
            }
        }

        while (true) {
            try {
                System.out.println("\n--- МЕНЮ (" + currentUser.getRole() + ") ---");
                System.out.println("1. Создать Image Post");
                System.out.println("2. Создать Video Post");
                System.out.println("3. Создать Story Post");
                System.out.println("4. Показать все посты");
                System.out.println("5. Удалить пост по ID");
                System.out.println("6. Выход");
                System.out.print("Выберите действие: ");

                String choice = scanner.nextLine();

                switch (choice) {
                    case "1": // Создание Image Post
                        // Проверка прав (Только для ADMIN)
                        if (!currentUser.getRole().equals("ADMIN")) {
                            System.out.println("ОШИБКА: У вас нет прав для СОЗДАНИЯ контента!");
                            break;
                        }
                        // Код создания поста...
                        System.out.print("Введите ID: ");
                        int imgId = Integer.parseInt(scanner.nextLine());
                        // ... (остальной ввод данных)
                        manager.addPost(new ImagePost(imgId, "Content", "Date", "Insta", "path/to/img", "Vintage"));
                        break;

                    case "2": // Создание Video Post
                        if (!currentUser.getRole().equals("ADMIN")) {
                            System.out.println("ОШИБКА: У вас нет прав для СОЗДАНИЯ видео!");
                            break;
                        }
                        // Код создания видео-поста...
                        break;

                    case "3": // Создание Story Post
                        if (!currentUser.getRole().equals("ADMIN")) {
                            System.out.println("ОШИБКА: У вас нет прав для СОЗДАНИЯ сторис!");
                            break;
                        }
                        // Код создания сторис...
                        break;

                    case "4": // Просмотр (Доступно ВСЕМ)
                        System.out.println("--- Список всех постов ---");
                        manager.showAllPosts();
                        break;

                    case "5": // Удаление
                        // Проверка прав (Только для ADMIN)
                        if (!currentUser.getRole().equals("ADMIN")) {
                            System.out.println("ОШИБКА: У вас нет прав для УДАЛЕНИЯ! Обратитесь к администратору.");
                            break;
                        }
                        System.out.print("Введите ID для удаления: ");
                        int idDel = Integer.parseInt(scanner.nextLine());
                        manager.deletePost(idDel);
                        break;

                    case "6":
                        System.out.println("Завершение сессии " + currentUser.getUsername());
                        return;

                    default:
                        System.out.println("Неверный выбор.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
                System.out.println("Пожалуйста, попробуйте снова.");
            }
        }
    }
}