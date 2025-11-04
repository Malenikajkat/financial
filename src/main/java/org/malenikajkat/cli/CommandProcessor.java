package org.malenikajkat.cli;

import org.malenikajkat.model.User;
import org.malenikajkat.service.auth.AuthService;
import org.malenikajkat.service.finance.FinanceService;
import org.malenikajkat.service.notification.NotificationService;
import org.malenikajkat.exception.ServiceException;

import java.util.Scanner;

public class CommandProcessor {

    private final Scanner scanner;
    private final AuthService authService;
    private final FinanceService financeService;
    private final NotificationService notificationService;
    private User loggedInUser;

    public CommandProcessor(Scanner scanner, AuthService authService, FinanceService financeService, NotificationService notificationService) {
        this.scanner = scanner;
        this.authService = authService;
        this.financeService = financeService;
        this.notificationService = notificationService;
    }

    public void run() {
        System.out.println("--- Приветствуем Вас в приложении управления финансами ---");
        while (true) {
            printMenu();
            handleCommand(getNextCommand());
        }
    }

    private void handleCommand(String command) {
        switch (command) {
            case "регистрация":
                registerNewUser();
                break;
            case "войти":
                logInUser();
                break;
            case "добавить доход":
                addIncome();
                break;
            case "добавить расход":
                addExpense();
                break;
            case "установить бюджет":
                setBudget();
                break;
            case "отчёт":
                displayFinancialReport();
                break;
            case "уведомления":
                checkNotifications();
                break;
            case "выйти":
                logout();
                break;
            case "помощь":
                printHelp();
                break;
            case "выход":
                exitApplication();
                break;
            default:
                System.out.println("Невалидная команда. Воспользуйтесь командой 'помощь'.");
        }
    }

    private void registerNewUser() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        try {
            authService.register(username, password); // Добавляем блок try-catch
        } catch (ServiceException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    private void logInUser() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        try {
            loggedInUser = authService.authenticate(username, password); // Добавляем блок try-catch
        } catch (ServiceException e) {
            System.err.println("Ошибка авторизации: " + e.getMessage());
        }
    }

    private void addIncome() {
        if (loggedInUser == null) {
            System.out.println("Сначала войдите в систему.");
            return;
        }
        System.out.print("Введите сумму дохода: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Введите категорию дохода: ");
        String category = scanner.nextLine();
        try {
            financeService.addIncome(loggedInUser, amount, category); // Добавляем блок try-catch
        } catch (ServiceException e) {
            System.err.println("Ошибка добавления дохода: " + e.getMessage());
        }
    }

    private void addExpense() {
        if (loggedInUser == null) {
            System.out.println("Сначала войдите в систему.");
            return;
        }
        System.out.print("Введите сумму расхода: ");
        double amount = Double.parseDouble(scanner.nextLine());
        System.out.print("Введите категорию расхода: ");
        String category = scanner.nextLine();
        try {
            financeService.addExpense(loggedInUser, amount, category); // Добавляем блок try-catch
        } catch (ServiceException e) {
            System.err.println("Ошибка добавления расхода: " + e.getMessage());
        }
    }

    private void setBudget() {
        if (loggedInUser == null) {
            System.out.println("Сначала войдите в систему.");
            return;
        }
        System.out.print("Введите название категории: ");
        String category = scanner.nextLine();
        System.out.print("Введите лимит бюджета: ");
        double limit = Double.parseDouble(scanner.nextLine());
        try {
            financeService.setBudget(loggedInUser, category, limit); // Добавляем блок try-catch
        } catch (ServiceException e) {
            System.err.println("Ошибка установки бюджета: " + e.getMessage());
        }
    }

    private void displayFinancialReport() {
        if (loggedInUser == null) {
            System.out.println("Сначала войдите в систему.");
            return;
        }
        var report = financeService.generateReport(loggedInUser); // Добавляем блок try-catch
        System.out.println(report);
    }

    private void checkNotifications() {
        if (loggedInUser == null) {
            System.out.println("Сначала войдите в систему.");
            return;
        }
        notificationService.notify(loggedInUser, "Проверка уведомлений успешно выполнена.");
    }

    private void logout() {
        loggedInUser = null;
        System.out.println("Вы вышли из системы.");
    }

    private void exitApplication() {
        System.out.println("Приложение закрывается...");
        System.exit(0);
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("- регистрация");
        System.out.println("- войти");
        System.out.println("- добавить доход");
        System.out.println("- добавить расход");
        System.out.println("- установить бюджет");
        System.out.println("- отчёт");
        System.out.println("- уведомления");
        System.out.println("- выйти");
        System.out.println("- помощь");
        System.out.println("- выход");
    }

    private String getNextCommand() {
        System.out.print("Введите команду: ");
        return scanner.nextLine().trim().toLowerCase();
    }

    private void printMenu() {
        System.out.println("\nЧто бы вы хотели сделать?");
        System.out.println("1. Зарегистрироваться");
        System.out.println("2. Войти в систему");
        System.out.println("3. Добавить доход");
        System.out.println("4. Добавить расход");
        System.out.println("5. Установить бюджет");
        System.out.println("6. Просмотреть отчёт");
        System.out.println("7. Проверить уведомления");
        System.out.println("8. Выйти из аккаунта");
        System.out.println("9. Справка");
        System.out.println("0. Выход из приложения");
    }
}