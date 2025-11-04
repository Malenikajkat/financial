package org.malenikajkat.service.budget;

import org.malenikajkat.model.Budget;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.util.ValidatorService;

public class BudgetServiceImpl implements BudgetService {

    private final ValidatorService validatorService;

    public BudgetServiceImpl(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    @Override
    public void setBudget(User user, String category, double limit) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории бюджета");
        validatorService.validatePositiveFloat(limit, "лимита бюджета");

        Budget budget = new Budget(category, limit);
        user.addBudget(budget);
    }

    @Override
    public Budget getBudget(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории бюджета");

        return user.getBudget(category);
    }

    @Override
    public boolean hasBudget(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории бюджета");

        return user.hasBudget(category);
    }

    @Override
    public void removeBudget(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории бюджета");

        if (!user.hasBudget(category)) {
            throw new ServiceException("Бюджет для категории '" + category + "' не найден");
        }
        user.removeBudget(category);
    }

    @Override
    public void resetSpent(User user, String category) throws ServiceException {
        validateUser(user);
        validatorService.validateCategory(category, "категории бюджета");

        Budget budget = user.getBudget(category);
        if (budget == null) {
            throw new ServiceException("Бюджет для категории '" + category + "' не найден");
        }
        budget.resetSpent();
    }

    private void validateUser(User user) throws ServiceException {
        if (user == null) {
            throw new ServiceException("Пользователь не может быть null");
        }
    }
}
