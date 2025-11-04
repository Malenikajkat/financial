package org.malenikajkat.cli;

import org.malenikajkat.service.auth.AuthService;
import org.malenikajkat.service.auth.AuthServiceImpl;
import org.malenikajkat.service.budget.BudgetService;
import org.malenikajkat.service.budget.BudgetServiceImpl;
import org.malenikajkat.service.category.CategoryService;
import org.malenikajkat.service.category.CategoryServiceImpl;
import org.malenikajkat.service.expense.ExpenseService;
import org.malenikajkat.service.expense.ExpenseServiceImpl;
import org.malenikajkat.service.finance.FinanceService;
import org.malenikajkat.service.finance.FinanceServiceImpl;
import org.malenikajkat.service.income.IncomeService;
import org.malenikajkat.service.income.IncomeServiceImpl;
import org.malenikajkat.service.persistence.InMemoryUserStorage;
import org.malenikajkat.service.persistence.UserStorage;
import org.malenikajkat.service.report.ReportGenerator;
import org.malenikajkat.service.report.ReportGeneratorImpl;
import org.malenikajkat.export_import.ExportService;
import org.malenikajkat.export_import.ExportServiceImpl;
import org.malenikajkat.export_import.ImportService;
import org.malenikajkat.export_import.ImportServiceImpl;
import org.malenikajkat.util.ValidatorService;

public class App {
    public static void main(String[] args) {
        // Используем конкретную реализацию
        UserStorage storage = new InMemoryUserStorage();
        ValidatorService validatorService = new ValidatorService();
        AuthService authService = new AuthServiceImpl(storage);
        ReportGenerator reportGenerator = new ReportGeneratorImpl();
        BudgetService budgetService = new BudgetServiceImpl(validatorService);
        CategoryService categoryService = new CategoryServiceImpl(validatorService);

        FinanceService financeService = new FinanceServiceImpl(validatorService, budgetService, categoryService);
        IncomeService incomeService = new IncomeServiceImpl(validatorService, categoryService);
        ExpenseService expenseService = new ExpenseServiceImpl(validatorService, categoryService, budgetService);

        ExportService exportService = new ExportServiceImpl();
        ImportService importService = new ImportServiceImpl();

        CommandProcessor processor = new CommandProcessor(
                authService,
                reportGenerator,
                financeService,
                incomeService,
                expenseService,
                budgetService,
                exportService,
                importService
        );

        processor.start();
    }
}