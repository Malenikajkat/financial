package org.malenikajkat.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;
import org.malenikajkat.export_import.ExportService;
import org.malenikajkat.export_import.ImportService;
import org.malenikajkat.model.AuthenticationResult;
import org.malenikajkat.model.Report;
import org.malenikajkat.model.User;
import org.malenikajkat.service.auth.AuthService;
import org.malenikajkat.service.budget.BudgetService;
import org.malenikajkat.service.expense.ExpenseService;
import org.malenikajkat.service.finance.FinanceService;
import org.malenikajkat.service.income.IncomeService;
import org.malenikajkat.service.report.ReportGenerator;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Scanner;

import static org.malenikajkat.util.ConfigLoader.USERS_FILE;

public class CommandProcessor {
    private final AuthService authService;
    private final ReportGenerator reportGenerator;
    private final FinanceService financeService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final ExportService exportService;
    private final ImportService importService;
    private final Scanner scanner;
    private User loggedInUser;

    public CommandProcessor(
            AuthService authService,
            ReportGenerator reportGenerator,
            FinanceService financeService,
            IncomeService incomeService,
            ExpenseService expenseService,
            BudgetService budgetService,
            ExportService exportService,
            ImportService importService
    ) {
        this.authService = authService;
        this.reportGenerator = reportGenerator;
        this.financeService = financeService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.exportService = exportService;
        this.importService = importService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        showWelcomeScreen();
        processCommands();
    }

    protected void showWelcomeScreen() {
        System.out.println("\nДобро пожаловать в приложение финансового учета!");
        System.out.println("Что бы вы хотели сделать?");
        System.out.println("1. Зарегистрироваться");
        System.out.println("2. Войти в аккаунт");
        System.out.println("3. Закрыть приложение");
    }

    protected void processCommands() {
        while (true) {
            System.out.print("\nВаш выбор: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Пожалуйста, введите значение.");
                continue;
            }

            switch (choice) {
                case "1":
                    handleRegistration();
                    break;
                case "2":
                    handleLogin();
                    break;
                case "3":
                    closeApplication();
                    break;
                default:
                    System.out.println("Неправильный выбор. Повторите попытку.");
            }
        }
    }

    protected void handleRegistration() {
        try {
            String login = promptFor("логин");
            String email = promptFor("email");
            String password = promptFor("пароль");

            authService.register(login, email, password);
            System.out.println("Вы успешно зарегистрированы. Теперь можете войти в аккаунт.");
        } catch (ServiceException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при вводе данных. Попробуйте ещё раз.");
        }
    }

    private void handleLogin() {
        try {
            String login = promptFor("логин");
            String password = promptFor("пароль");

            AuthenticationResult result = authService.authenticate(login, password);
            if (result.isSuccess()) {
                loggedInUser = result.getAuthenticatedUser();
                System.out.println("Вы вошли в систему как " + loggedInUser.getLogin());
                displayFinancialMenu();
            } else {
                System.out.println(result.getMessage());
            }
        } catch (ServiceException e) {
            System.err.println("Ошибка входа: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при вводе данных. Попробуйте ещё раз.");
        }
    }

    private void closeApplication() {
        System.out.println("Приложение закрыто. До новых встреч!");
        scanner.close();
        System.exit(0);
    }

    private String promptFor(String fieldName) {
        return promptFor(fieldName, "");
    }

    private String promptFor(String fieldName, String formatHint) {
        System.out.print("Введите " + fieldName + ": ");
        if (!formatHint.isEmpty()) {
            System.out.print(" (" + formatHint + ")");
        }
        System.out.print(": ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.err.println(fieldName + " не может быть пустым.");
            return promptFor(fieldName, formatHint);
        }
        return input;
    }

    private void displayFinancialMenu() {
        while (loggedInUser != null) {
            System.out.println("\nФинансовое меню:");
            System.out.println("1. Просмотреть отчёт");
            System.out.println("2. Экспортировать отчёт");
            System.out.println("3. Импортировать отчёт");
            System.out.println("4. Добавить доход");
            System.out.println("5. Добавить расход");
            System.out.println("6. Установить бюджет");
            System.out.println("7. Проверить баланс");
            System.out.println("8. Завершить сеанс");
            System.out.println("9. Удалить аккаунт");

            System.out.print("Ваш выбор: ");
            String choice = scanner.nextLine().trim();

            if (choice.isEmpty()) {
                System.out.println("Пожалуйста, введите значение.");
                continue;
            }

            switch (choice) {
                case "1":
                    viewReport();
                    break;
                case "2":
                    exportReport();
                    break;
                case "3":
                    importReport();
                    break;
                case "4":
                    addIncome();
                    break;
                case "5":
                    addExpense();
                    break;
                case "6":
                    setBudget();
                    break;
                case "7":
                    checkBalance();
                    break;
                case "8":
                    logout();
                    break;
                case "9":
                    deleteAccount();
                    break;
                default:
                    System.out.println("Неправильный выбор. Повторите попытку.");
            }
        }
    }

    private void deleteAccount() {
        try {
            System.out.print("Вы уверены, что хотите удалить аккаунт '" + loggedInUser.getLogin() + "'? (да/нет): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (!confirm.equals("да")) {
                System.out.println("Удаление отменено.");
                return;
            }

            deleteUserFromJson(loggedInUser.getLogin());
            System.out.println("Аккаунт '" + loggedInUser.getLogin() + "' успешно удалён.");
            logout();
        } catch (IOException e) {
            System.err.println("Ошибка при удалении аккаунта: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при удалении аккаунта. Попробуйте ещё раз.");
        }
    }

    private void deleteUserFromJson(String login) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            throw new IOException("Файл пользователей не найден: " + USERS_FILE);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> users = (Map<String, Object>) mapper.readValue(file, Map.class);

        if (!users.containsKey(login)) {
            throw new IOException("Пользователь с логином '" + login + "' не найден в файле.");
        }

        users.remove(login);
        System.out.println("Пользователь '" + login + "' удалён из данных.");

        mapper.writeValue(file, users);
    }

    private void viewReport() {
        try {
            LocalDate startDate = parseDate(promptFor("начальную дату", "YYYY-MM-DD"));
            LocalDate endDate = parseDate(promptFor("конечную дату", "YYYY-MM-DD"));

            Report report = reportGenerator.generateSummaryReport(loggedInUser, startDate, endDate);

            System.out.println(report.toString());
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка формата даты. Используйте формат YYYY-MM-DD.");
        } catch (ServiceException e) {
            System.err.println("Ошибка при просмотре отчёта: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при вводе данных. Попробуйте ещё раз.");
        }
    }

    private void exportReport() {
        try {
            String path = promptFor("путь для сохранения отчета (CSV или JSON)");

            if (path.endsWith(".csv")) {
                exportService.exportCsv(reportGenerator.generateReport(loggedInUser), path);
            } else if (path.endsWith(".json")) {
                exportService.exportJson(reportGenerator.generateReport(loggedInUser), path);
            } else {
                System.out.println("Формат файла не поддерживается.");
            }

            System.out.println("Отчёт успешно экспортирован.");
        } catch (IOException | ServiceException e) {
            System.err.println("Ошибка экспорта отчёта: " + e.getMessage());
        }
    }

    private void importReport() {
        try {
            String path = promptFor("путь к файлу отчёта (CSV или JSON)");

            if (path.endsWith(".csv")) {
                Report importedReport = importService.importCsv(path);
                System.out.println("Отчёт успешно импортирован из CSV.");
            } else if (path.endsWith(".json")) {
                Report importedReport = importService.importJson(path);
                System.out.println("Отчёт успешно импортирован из JSON.");
            } else {
                System.out.println("Формат файла не поддерживается.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка импорта отчёта: " + e.getMessage());
        }
    }

    private void addIncome() {
        try {
            double amount = readPositiveDouble("сумму дохода");
            String category = promptFor("категорию дохода");
            incomeService.addIncome(loggedInUser, amount, category);
            System.out.println("Доход успешно добавлен.");
        } catch (NumberFormatException e) {
            System.err.println("Сумма должна быть числом. Используйте формат: 1000.50");
        } catch (ServiceException | ValidationException e) {
            System.err.println("Ошибка при добавлении дохода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при вводе данных. Попробуйте ещё раз.");
        }
    }

    private void addExpense() {
        try {
            double amount = readPositiveDouble("сумму расхода");
            String category = promptFor("категорию расхода");
            expenseService.addExpense(loggedInUser, amount, category);
            System.out.println("Расход успешно добавлен.");
        } catch (NumberFormatException e) {
            System.err.println("Сумма должна быть числом. Используйте формат: 1000.50");
        } catch (ServiceException | ValidationException e) {
            System.err.println("Ошибка при добавлении расхода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при вводе данных. Попробуйте ещё раз.");
        }
    }

    private void setBudget() {
        try {
            String category = promptFor("категорию бюджета");
            double limit = readPositiveDouble("лимит бюджета");
            budgetService.setBudget(loggedInUser, category, limit);
            System.out.println("Бюджет успешно установлен для категории '" + category + "' с лимитом " + limit);
        } catch (NumberFormatException e) {
            System.err.println("Лимит должен быть числом. Используйте формат: 1000.50");
        } catch (ServiceException | ValidationException e) {
            System.err.println("Ошибка при установке бюджета: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при вводе данных. Попробуйте ещё раз.");
        }
    }

    private void checkBalance() {
        try {
            double totalIncome = financeService.getTotalByCategory(loggedInUser, "", true);
            double totalExpense = financeService.getTotalByCategory(loggedInUser, "", false);
            double balance = totalIncome - totalExpense;

            System.out.printf("Ваш текущий баланс: %.2f\n", balance);
            System.out.printf("Общий доход: %.2f\n", totalIncome);
            System.out.printf("Общие расходы: %.2f\n", totalExpense);
        } catch (ServiceException | ValidationException e) {
            System.err.println("Ошибка при проверке баланса: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Произошла ошибка при расчёте баланса. Попробуйте ещё раз.");
        }
    }

    private void logout() {
        loggedInUser = null;
        System.out.println("Вы вышли из системы.");
        showWelcomeScreen();
    }

    private double readPositiveDouble(String fieldName) {
        System.out.print("Введите " + fieldName + ": ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.err.println(fieldName + " не может быть пустым.");
            return readPositiveDouble(fieldName);
        }
        double amount = Double.parseDouble(input);
        if (amount <= 0) {
            System.err.println(fieldName + " должна быть положительным числом.");
            return readPositiveDouble(fieldName);
        }
        return amount;
    }

    private LocalDate parseDate(String input) {
        return LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE);
    }

}