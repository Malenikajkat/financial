package org.malenikajkat.service.expense;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;

import java.util.List;

public interface ExpenseService {

    void addExpense(User user, double amount, String category) throws ServiceException, ValidationException;

    List<Transaction> getAllExpenses(User user) throws ServiceException, ValidationException;

    List<Transaction> getExpensesByCategory(User user, String category) throws ServiceException, ValidationException;

    double getTotalExpense(User user) throws ServiceException, ValidationException;

    double getTotalExpenseByCategory(User user, String category) throws ServiceException, ValidationException;

    boolean hasExpenseInCategory(User user, String category) throws ServiceException, ValidationException;

    void resetAllExpenses(User user) throws ServiceException, ValidationException;
}