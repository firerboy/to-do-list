package ru.artem.to_do.list;

import java.sql.*;
import java.util.Scanner;

public class ToDoList {
    static final String DB_URL = "jdbc:postgresql://localhost:5432/xxxx";
    static final String USER = "xxxx";
    static final String PASS = "xxxx";
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            while (true) {
                System.out.println("Список дел:");
                System.out.println("1. Добавить задачу");
                System.out.println("2. Просмотреть задачи");
                System.out.println("3. Изменить задачу");
                System.out.println("4. Удалить задачу");
                System.out.println("5. Выход");

                System.out.print("Выберите действие: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Введите задачу: ");
                        String task = scanner.nextLine();
                        addTask(conn, task);
                        break;
                    case 2:
                        viewTasks(conn);
                        break;
                    case 3:
                        updateTask(conn);
                        break;
                    case 4:
                        deleteTask(conn);
                        break;
                    case 5:
                        System.out.println("До свидания!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Неверный выбор!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }

    static void addTask(Connection conn, String task) {
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (task) VALUES (?)")) {
            stmt.setString(1, task);
            stmt.executeUpdate();
            System.out.println("Задача добавлена!");
        } catch (SQLException e) {
            System.out.println("Ошибка добавления задачи: " + e.getMessage());
        }
    }

    static void viewTasks(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            if (!rs.next()) {
                System.out.println("Список дел пуст.");
            } else {
                System.out.println("Список дел:");
                do {
                    System.out.println(rs.getInt("id") + ". " + rs.getString("task"));
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println("Ошибка просмотра задач: " + e.getMessage());
        }
    }

    static void updateTask(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            viewTasks(conn);

            System.out.print("Введите ID задачи для изменения: ");
            int taskId = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера ввода

            System.out.print("Введите новый текст задачи: ");
            String newTask = scanner.nextLine();

            stmt.executeUpdate("UPDATE tasks SET task = '" + newTask + "' WHERE id = " + taskId);
            System.out.println("Задача изменена!");
        } catch (SQLException e) {
            System.out.println("Ошибка изменения задачи: " + e.getMessage());
        }
    }

    static void deleteTask(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            viewTasks(conn);

            System.out.print("Введите ID задачи для удаления: ");
            int taskId = scanner.nextInt();
            scanner.nextLine();

            stmt.executeUpdate("DELETE FROM tasks WHERE id = " + taskId);
            System.out.println("Задача удалена!");
        } catch (SQLException e) {
            System.out.println("Ошибка удаления задачи: " + e.getMessage());
        }
    }
}