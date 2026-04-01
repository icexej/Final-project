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
                            System.out.print("Дата: "); String dateI = readValidDate(scanner);
                            System.out.print("Платформа: "); String platI = readPlatform(scanner);
                            System.out.print("Путь к фото: "); String urlI = scanner.nextLine();
                            manager.addPost(new ImagePost(idI, contI, dateI, platI, urlI));
                            break;

                        case "4": // VIDEO POST
                            int idV = readUniqueId(scanner, manager);
                            System.out.print("Текст: "); String contV = scanner.nextLine();
                            System.out.print("Дата: "); String dateV = readValidDate(scanner);
                            System.out.print("Платформа: "); String platV = readPlatform(scanner);
                            System.out.print("Длительность (мин): "); double dur = Double.parseDouble(scanner.nextLine());
                            manager.addPost(new VideoPost(idV, contV, dateV, platV, dur));
                            break;

                        case "5": // STORY POST
                            int idS = readUniqueId(scanner, manager);
                            System.out.print("Текст: "); String contS = scanner.nextLine();
                            System.out.print("Дата: "); String dateS = readValidDate(scanner);
                            System.out.print("Платформа: "); String platS = readPlatform(scanner);
                            System.out.print("Для близких? (да/нет): "); boolean isClose = readYesNo(scanner);
                            manager.addPost(new StoryPost(idS, contS, dateS, platS, isClose));
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
    private static String readValidDate(Scanner scanner) {
        while (true) {
            System.out.print("Введите дату (дд.мм.гггг): ");
            String date = scanner.nextLine();

            // 1. Сначала проверяем общий формат через Regex (цифры.цифры.цифры)
            if (!date.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
                System.out.println("❌ Ошибка: Формат должен быть дд.мм.гггг (например, 12.05.2024)");
                continue;
            }

            try {
                // 2. Разрезаем строку по точкам
                String[] parts = date.split("\\.");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                // 3. Проверяем логику чисел
                if (day < 1 || day > 31) {
                    System.out.println("❌ Ошибка: День должен быть от 01 до 31!");
                } else if (month < 1 || month > 12) {
                    System.out.println("❌ Ошибка: Месяц должен быть от 01 до 12!");
                } else if (year < 2000 || year > 2026) {
                    System.out.println("❌ Ошибка: Год должен быть до 2026!");
                } else {
                    return date; // Если всё прошло, возвращаем дату
                }
            } catch (Exception e) {
                System.out.println("❌ Ошибка: Некорректные числа в дате.");
            }
        }
    }
    private static String readPlatform(Scanner scanner) {
        String[] platforms = {"Instagram", "Telegram", "TikTok", "Facebook", "Twitter (X)"};

        while (true) {
            System.out.println("Выберите платформу:");
            for (int i = 0; i < platforms.length; i++) {
                System.out.println((i + 1) + ". " + platforms[i]);
            }
            System.out.print("Ваш выбор (номер): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= platforms.length) {
                    return platforms[choice - 1]; // Возвращаем название по индексу
                } else {
                    System.out.println("❌ Ошибка: Выберите число от 1 до " + platforms.length);
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Ошибка: Введите число!");
            }
        }
    }
    private static boolean readYesNo(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("да")) {
                return true;
            } else if (input.equals("нет")) {
                return false;
            } else {
                System.out.println("❌ Ошибка: Введите только 'да' или 'нет'!");
            }
        }
    }
}
