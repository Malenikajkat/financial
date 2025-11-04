package org.malenikajkat.service.expense;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;

import java.util.List;

public interface ExpenseService {

    void addExpense(User user, double amount, String category) throws ServiceException;

    List<Transaction> getAllExpenses(User user) throws ServiceException;

    List<Transaction> getExpensesByCategory(User user, String category) throws ServiceException;

    double getTotalExpense(User user) throws ServiceException;

    double getTotalExpenseByCategory(User user, String category) throws ServiceException;

    boolean hasExpenseInCategory(User user, String category) throws ServiceException;

    void resetAllExpenses(User user) throws ServiceException;
}
