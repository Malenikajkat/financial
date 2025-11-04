package org.malenikajkat.service.budget;

import org.malenikajkat.model.Budget;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;

public interface BudgetService {

    void setBudget(User user, String category, double limit) throws ServiceException, ValidationException;

    Budget getBudget(User user, String category) throws ServiceException, ValidationException;

    boolean hasBudget(User user, String category) throws ServiceException, ValidationException;

    void removeBudget(User user, String category) throws ServiceException, ValidationException;

    void resetSpent(User user, String category) throws ServiceException, ValidationException;
}