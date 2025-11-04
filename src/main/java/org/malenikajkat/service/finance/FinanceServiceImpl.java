package org.malenikajkat.service.finance;

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
import java.util.Objects;

public class FinanceServiceImpl implements FinanceService {

    private final ValidatorService validatorService;
    private final BudgetService budgetService;
    private final CategoryService categoryService;

    public FinanceServiceImpl(ValidatorService validatorService, BudgetService budgetService, CategoryService categoryService) {
        this.validatorService = Objects.requireNonNull(validatorService, "ValidatorService не может быть null");
        this.budgetService = Objects.requireNonNull(budgetService, "BudgetService не может быть null");
        this.categoryService = Objects.requireNonNull(categoryService, "CategoryService не может быть null");
    }

    @Override
    public void addIncome(User user, double amount, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validatePositiveFloat(amount, "суммы дохода");
        validatorService.validateCategory(category, "категории дохода");

        if (!categoryService.hasCategory(user, category)) {
            categoryService.addCategory(user, category);
        }

        user.getWallet().addIncome(amount, category, LocalDate.now());
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
    public List<Transaction> getAllTransactions(User user) throws ServiceException, ValidationException {
        validateUser(user);
        return user.getWallet().getTransactions();
    }

    @Override
    public List<Transaction> getTransactionsByType(User user, boolean isIncome) throws ServiceException, ValidationException {
        validateUser(user);
        Transaction.Type type = isIncome ? Transaction.Type.INCOME : Transaction.Type.EXPENSE;
        return user.getWallet().getTransactionsByType(type);
    }

    @Override
    public double getTotalByCategory(User user, String category, boolean isIncome) throws ServiceException, ValidationException {
        validateUser(user);
        if (category == null || category.trim().isEmpty()) {
            Transaction.Type type = isIncome ? Transaction.Type.INCOME : Transaction.Type.EXPENSE;
            return user.getWallet().getTotalAmountByType(type);
        }

        Transaction.Type type = isIncome ? Transaction.Type.INCOME : Transaction.Type.EXPENSE;
        return user.getWallet().getTotalByCategory(category, type);
    }

    @Override
    public void transfer(User sender, User receiver, double amount, String comment) throws ServiceException, ValidationException {
        validateUser(sender);
        validateUser(receiver);
        validatorService.validatePositiveFloat(amount, "суммы перевода");

        if (Objects.equals(sender, receiver)) {
            throw new ServiceException("Нельзя перевести деньги самому себе");
        }

        if (sender.getWallet().getBalance() < amount) {
            throw new ServiceException("Недостаточно средств у отправителя. Баланс: " +
                    sender.getWallet().getBalance() + ", сумма перевода: " + amount);
        }

        Transaction expenseTx = new Transaction(
                amount,
                "Перевод на счёт пользователя: " + receiver.getLogin(),
                "перевод",
                Transaction.Type.EXPENSE,
                LocalDate.now()
        );
        Transaction incomeTx = new Transaction(
                amount,
                "Получил перевод от пользователя: " + sender.getLogin(),
                "перевод",
                Transaction.Type.INCOME,
                LocalDate.now()
        );

        sender.getWallet().addTransaction(expenseTx);
        receiver.getWallet().addTransaction(incomeTx);


        if (comment != null && !comment.trim().isEmpty()) {
            Transaction commentTx = new Transaction(
                    0,
                    "Комментарий к переводу: " + comment,
                    "комментарий",
                    Transaction.Type.INCOME,
                    LocalDate.now()
            );
            sender.getWallet().addTransaction(commentTx);
            receiver.getWallet().addTransaction(commentTx);
        }
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
    }
}
