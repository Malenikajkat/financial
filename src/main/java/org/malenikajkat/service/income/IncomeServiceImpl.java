package org.malenikajkat.service.income;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;
import org.malenikajkat.service.category.CategoryService;
import org.malenikajkat.util.ValidatorService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IncomeServiceImpl implements IncomeService {

    private final ValidatorService validatorService;
    private final CategoryService categoryService;

    public IncomeServiceImpl(ValidatorService validatorService, CategoryService categoryService) {
        this.validatorService = Objects.requireNonNull(validatorService,
                "ValidatorService не может быть null");
        this.categoryService = Objects.requireNonNull(categoryService,
                "CategoryService не может быть null");
    }

    @Override
    public void addIncome(User user, double amount, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validatePositiveFloat(amount, "Сумма дохода должна быть положительной");
        validatorService.validateCategory(category, "Название категории не должно быть пустым");

        if (!categoryService.hasCategory(user, category)) {
            categoryService.addCategory(user, category);
        }

        user.getWallet().addIncome(amount, category, LocalDate.now());
    }

    @Override
    public List<Transaction> getAllIncomes(User user) throws ServiceException, ValidationException {
        validateUser(user);
        return user.getWallet().getTransactionsByType(Transaction.Type.INCOME);
    }

    @Override
    public List<Transaction> getIncomesByCategory(User user, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(category, "Название категории не должно быть пустым");

        List<Transaction> transactions = user.getWallet().getTransactionsByType(Transaction.Type.INCOME);
        return transactions.stream()
                .filter(transaction -> transaction.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalIncome(User user) throws ServiceException, ValidationException {
        validateUser(user);
        return user.getWallet().getTotalAmountByType(Transaction.Type.INCOME);
    }

    @Override
    public double getTotalIncomeByCategory(User user, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(category, "Название категории не должно быть пустым");

        return user.getWallet().getTransactionsByType(Transaction.Type.INCOME)
                .stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    @Override
    public boolean hasIncomeInCategory(User user, String category) throws ServiceException, ValidationException {
        validateUser(user);
        validatorService.validateCategory(category, "Название категории не должно быть пустым");

        return user.getWallet().getTransactionsByType(Transaction.Type.INCOME)
                .stream()
                .anyMatch(t -> t.getCategory().equalsIgnoreCase(category));
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
        if (user.getWallet() == null) {
            throw new ServiceException("Кошелёк пользователя не может быть null");
        }
    }
}
