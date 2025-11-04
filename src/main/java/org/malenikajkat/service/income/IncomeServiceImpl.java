package org.malenikajkat.service.income;

import org.malenikajkat.model.*;
import org.malenikajkat.service.category.CategoryService;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.util.ValidatorService;

import java.util.*;
import java.util.stream.Collectors;

public class IncomeServiceImpl implements IncomeService {

    private final ValidatorService validatorService;
    private final CategoryService categoryService;

    public IncomeServiceImpl(ValidatorService validatorService,
                             CategoryService categoryService) {
        this.validatorService = validatorService;
        this.categoryService = categoryService;
    }

    @Override
    public void addIncome(User user, double amount, String category) throws ServiceException {
        validateUser(user);
        validatorService.validatePositiveFloat(amount, "суммы дохода");
        validatorService.validateCategory(category, "категории дохода");

        if (!categoryService.hasCategory(user, category)) {
            categoryService.addCategory(user, category);
        }

        user.getWallet().addIncome(amount, category);
    }

    @Override
    public List<Transaction> getAllIncomes(User user) throws ServiceException {
        validateUser(user);
        return user.getWallet().getTransactionsByType(true);
    }

    @Override
    public List<Transaction> getIncomesByCategory(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории доходов");

        List<Transaction> allIncomes = user.getWallet().getTransactionsByType(true);
        return allIncomes.stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalIncome(User user) throws ServiceException {
        validateUser(user);
        return user.getWallet().getTotalIncome();
    }

    @Override
    public double getTotalIncomeByCategory(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории доходов");

        return user.getWallet().getTotalByCategory(category, true);
    }

    @Override
    public boolean hasIncomeInCategory(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории доходов");

        List<Transaction> incomes = getIncomesByCategory(user, category);
        return !incomes.isEmpty();
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
    }
}
