package org.malenikajkat.service.budget;

import org.malenikajkat.model.Budget;
import org.malenikajkat.model.User;
import org.malenikajkat.exception.ServiceException;

public interface BudgetService {

    void setBudget(User user, String category, double limit) throws ServiceException;

    Budget getBudget(User user, String category) throws ServiceException;

    boolean hasBudget(User user, String category) throws ServiceException;

    void removeBudget(User user, String category) throws ServiceException;

    void resetSpent(User user, String category) throws ServiceException;
}
