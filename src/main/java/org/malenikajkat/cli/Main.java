package org.malenikajkat.cli;

import org.malenikajkat.service.auth.AuthService;
import org.malenikajkat.service.budget.BudgetService;
import org.malenikajkat.service.category.CategoryService;
import org.malenikajkat.service.expense.ExpenseService;
import org.malenikajkat.service.finance.FinanceService;
import org.malenikajkat.service.income.IncomeService;
import org.malenikajkat.service.notification.ConsoleNotificationService;
import org.malenikajkat.service.notification.NotificationService;
import org.malenikajkat.service.report.ReportGenerator;
import org.malenikajkat.util.ValidatorService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        AuthService authService = new AuthService();
        FinanceService financeService = new FinanceService(authService, new BudgetService(), new CategoryService(),
                new ExpenseService(), new IncomeService(), new ReportGenerator());
        NotificationService notificationService = new ConsoleNotificationService();
        ValidatorService validatorService = new ValidatorService();

        Scanner scanner = new Scanner(System.in);

        CommandProcessor processor = new CommandProcessor(scanner, authService, financeService, notificationService);

        System.out.println("*** Добро пожаловать в приложение управления финансами ***");
        processor.run();
        scanner.close();
        System.out.println("Завершаем работу приложения. Спасибо за использование!");
    }
}