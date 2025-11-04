package org.malenikajkat.service.finance;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;

import java.util.List;

public interface FinanceService {

    void addIncome(User user, double amount, String category) throws ServiceException;

    void addExpense(User user, double amount, String category) throws ServiceException;

    List<Transaction> getAllTransactions(User user) throws ServiceException;

    List<Transaction> getTransactionsByType(User user, boolean isIncome) throws ServiceException;

    double getTotalByCategory(User user, String category, boolean isIncome) throws ServiceException;

    void transfer(User sender, User receiver, double amount, String comment) throws ServiceException;
}
