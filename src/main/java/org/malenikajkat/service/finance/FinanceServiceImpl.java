package org.malenikajkat.service.finance;

import org.malenikajkat.model.*;
import org.malenikajkat.service.budget.BudgetService;
import org.malenikajkat.service.category.CategoryService;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.util.ValidatorService;

import java.util.List;
import java.util.Objects;

public class FinanceServiceImpl implements FinanceService {

    private final ValidatorService validatorService;
    private final BudgetService budgetService;
    private final CategoryService categoryService;

    public FinanceServiceImpl(ValidatorService validatorService,
                              BudgetService budgetService,
                              CategoryService categoryService) {
        this.validatorService = validatorService;
        this.budgetService = budgetService;
        this.categoryService = categoryService;
    }

    @Override
    public void addIncome(User user, double amount, String category) throws ServiceException {
        validateUser(user);
        validatorService.validatePositiveFloat(amount, "суммы дохода");
        validatorService.validateCategory(category, "категории дохода");

        // Автоматически добавляем категорию, если её нет
        if (!categoryService.hasCategory(user, category)) {
            categoryService.addCategory(user, category);
        }

        user.getWallet().addIncome(amount, category);
    }

    @Override
    public void addExpense(User user, double amount, String category) throws ServiceException {
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

        user.getWallet().addExpense(amount, category);
    }

    @Override
    public List<Transaction> getAllTransactions(User user) throws ServiceException {
        validateUser(user);
        return user.getWallet().getTransactions();
    }

    @Override
    public List<Transaction> getTransactionsByType(User user, boolean isIncome) throws ServiceException {
        validateUser(user);
        return user.getWallet().getTransactionsByType(isIncome);
    }

    @Override
    public double getTotalByCategory(User user, String category, boolean isIncome) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории транзакций");

        return user.getWallet().getTotalByCategory(category, isIncome);
    }

    @Override
    public void transfer(User sender, User receiver, double amount, String comment) throws ServiceException {
        validateUser(sender);
        validateUser(receiver);
        validatorService.validatePositiveFloat(amount, "суммы перевода");

        if (sender.equals(receiver)) {
            throw new ServiceException("Нельзя перевести средства самому себе");
        }

        if (sender.getWallet().getBalance() < amount) {
            throw new ServiceException("Недостаточно средств у отправителя. Баланс: " +
                    sender.getWallet().getBalance() + ", сумма перевода: " + amount);
        }

        sender.getWallet().addExpense(amount, "Перевод: " + receiver.getLogin());

        receiver.getWallet().addIncome(amount, "Перевод от: " + sender.getLogin());

        if (comment != null && !comment.trim().isEmpty()) {
            Transaction commentTx = new Transaction(0, "Комментарий к переводу: " + comment, true);
            sender.getWallet().transactions.add(commentTx);
            receiver.getWallet().transactions.add(commentTx);
        }
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
    }
}
