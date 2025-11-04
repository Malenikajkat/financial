package org.malenikajkat.service.expense;

import org.malenikajkat.model.Budget;
import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;
import org.malenikajkat.service.budget.BudgetService;
import org.malenikajkat.service.category.CategoryService;
import org.malenikajkat.util.ValidatorService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseServiceImpl implements ExpenseService {

    private final ValidatorService validatorService;
    private final CategoryService categoryService;
    private final BudgetService budgetService;

    public ExpenseServiceImpl(ValidatorService validatorService, CategoryService categoryService, BudgetService budgetService) {
        this.validatorService = validatorService;
        this.categoryService = categoryService;
        this.budgetService = budgetService;
    }

    @Override
    public void addExpense(User user, double amount, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validatePositiveFloat(amount, "суммы расхода");
        validatorService.validateCategory(category, "категории расхода");

        if (user.getWallet().getBalance() < amount) {
            throw new ServiceException("Недостаточно средств на счету. Баланс: " +
                    user.getWallet().getBalance() + ", запрашиваемая сумма: " + amount);
        }

        Budget budget = budgetService.getBudget(user, category);
        if (budget != null) {
            double newSpent = budget.getSpent() + amount;
            if (newSpent > budget.getLimit()) {
                throw new ServiceException("Превышение бюджета для категории '" + category +
                        "'. Лимит: " + budget.getLimit() + ", текущий расход: " + newSpent);
            }
            budget.addSpent(amount);
        }

        if (!categoryService.hasCategory(user, category)) {
            categoryService.addCategory(user, category);
        }

        user.getWallet().addExpense(amount, category, LocalDate.now());
    }

    @Override
    public List<Transaction> getAllExpenses(User user) throws ServiceException, ValidationException {
        validateUser(user);
        return user.getWallet().getTransactionsByType(Transaction.Type.EXPENSE);
    }

    @Override
    public List<Transaction> getExpensesByCategory(User user, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(category, "категории расходов");

        List<Transaction> allExpenses = user.getWallet().getTransactionsByType(Transaction.Type.EXPENSE);
        return allExpenses.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalExpense(User user) throws ServiceException, ValidationException {
        validateUser(user);
        return user.getWallet().getTotalExpense();
    }

    @Override
    public double getTotalExpenseByCategory(User user, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(category, "категории расходов");

        return user.getWallet().getTotalByCategory(category, Transaction.Type.EXPENSE);
    }

    @Override
    public boolean hasExpenseInCategory(User user, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(category, "категории расходов");

        List<Transaction> expenses = getExpensesByCategory(user, category);
        return !expenses.isEmpty();
    }

    @Override
    public void resetAllExpenses(User user) throws ServiceException, ValidationException {
        validateUser(user);

        List<Transaction> expenses = user.getWallet().getTransactionsByType(Transaction.Type.EXPENSE);
        for (Transaction expense : expenses) {
            user.getWallet().removeTransaction(expense);

            try {
                Budget budget = budgetService.getBudget(user, expense.getCategory());
                if (budget != null) {
                    budget.resetSpent();
                }
            } catch (ServiceException ignored) {}
        }
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
    }
}
