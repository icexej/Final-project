import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PostManager manager = new PostManager();
        manager.loadFromFile();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            User currentUser = null;

            System.out.println("\n========================================");
            System.out.println("   ДОБРО ПОЖАЛОВАТЬ В SOCIAL SCHEDULER   ");
            System.out.println("========================================");

            // --- СИСТЕМА ВХОДА ---
            while (currentUser == null) {
                System.out.println("\nВыберите тип входа:");
                System.out.println("1. Администратор (Пароль)");
                System.out.println("2. Гость (Свободный вход)");
                System.out.println("0. Завершить программу");
                System.out.print("Ваш выбор: ");

                String loginChoice = scanner.nextLine();

                if (loginChoice.equals("1")) {
                    System.out.print("Введите пароль: ");
                    if (scanner.nextLine().equals("2404")) {
                        currentUser = new User("Admin", "2404", "ADMIN");
                        System.out.println("✅ Доступ разрешен.");
                    } else {
                        System.out.println("❌ Неверный пароль!");
                    }
                } else if (loginChoice.equals("2")) {
                    System.out.print("Введите ваш никнейм: ");
                    String name = scanner.nextLine();
                    currentUser = new User(name.isEmpty() ? "Аноним" : name, "", "USER");
                    System.out.println("ℹ️ Добро пожаловать, " + currentUser.getUsername() + "!");
                } else if (loginChoice.equals("0")) {
                    return;
                }
            }

            // --- РАБОТА В АККАУНТЕ ---
            boolean isRunning = true;
            while (isRunning) {
                try {
                    System.out.println("\n--- МЕНЮ (" + currentUser.getUsername() + ") ---");
                    System.out.println("1. Просмотр всех постов");
                    System.out.println("2. Оставить комментарий");

                    if (currentUser.getRole().equals("ADMIN")) {
                        System.out.println("3. Создать Image Post");
                        System.out.println("4. Создать Video Post");
                        System.out.println("5. Создать Story Post");
                        System.out.println("6. Удалить пост");
                    }

                    System.out.println("9. Сменить пользователя");
                    System.out.println("0. Выход");
                    System.out.print("Выбор: ");

                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            manager.showAllPosts();
                            break;

                        case "2":
                            System.out.print("Введите ID поста: ");
                            int idComm = Integer.parseInt(scanner.nextLine());
                            Post found = manager.findPostById(idComm);
                            if (found != null) {
                                System.out.print("Ваш комментарий: ");
                                found.addComment(currentUser.getUsername(), scanner.nextLine());
                                manager.saveToFile();
                                System.out.println("✅ Готово!");
                            } else {
                                System.out.println("❌ Пост не найден.");
                            }
                            break;

                        case "3": // IMAGE POST
                            int idI = readUniqueId(scanner, manager);
                            System.out.print("Текст: "); String contI = scanner.nextLine();
                            System.out.print("Дата: "); String dateI = scanner.nextLine();
                            System.out.print("Платформа: "); String platI = scanner.nextLine();
                            System.out.print("Путь к фото: "); String urlI = scanner.nextLine();
                            manager.addPost(new ImagePost(idI, contI, dateI, platI, urlI));
                            break;

                        case "4": // VIDEO POST
                            int idV = readUniqueId(scanner, manager);
                            System.out.print("Текст: "); String contV = scanner.nextLine();
                            System.out.print("Дата: "); String dateV = scanner.nextLine();
                            System.out.print("Платформа: "); String platV = scanner.nextLine();
                            System.out.print("Длительность (мин): "); double dur = Double.parseDouble(scanner.nextLine());
                            manager.addPost(new VideoPost(idV, contV, dateV, platV, dur));
                            break;

                        case "5": // STORY POST
                            int idS = readUniqueId(scanner, manager);
                            System.out.print("Текст: "); String contS = scanner.nextLine();
                            System.out.print("Дата: "); String dateS = scanner.nextLine();
                            System.out.print("Платформа: "); String platS = scanner.nextLine();
                            System.out.print("Для близких? (true/false): "); boolean fr = Boolean.parseBoolean(scanner.nextLine());
                            manager.addPost(new StoryPost(idS, contS, dateS, platS, fr));
                            break;

                        case "6": // DELETE
                            System.out.print("ID для удаления: ");
                            manager.deletePost(Integer.parseInt(scanner.nextLine()));
                            break;

                        case "9":
                            isRunning = false;
                            break;

                        case "0":
                            return;

                        default:
                            System.out.println("❌ Ошибка.");
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ Ошибка ввода.");
                }
            }
        }
    }

    // Твой метод проверки ID
    private static int readUniqueId(Scanner scanner, PostManager manager) {
        while (true) {
            try {
                System.out.print("Введите уникальный ID: ");
                int id = Integer.parseInt(scanner.nextLine());
                if (id <= 0) {
                    System.out.println("⚠️ ID должен быть > 0");
                    continue;
                }
                if (manager.findPostById(id) != null) {
                    System.out.println("⚠️ ID уже занят!");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Введите число!");
            }
        }
    }
}