package org.malenikajkat.service.finance;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;

import java.util.List;

public interface FinanceService {

    void addIncome(User user, double amount, String category) throws ServiceException, ValidationException;

    void addExpense(User user, double amount, String category) throws ServiceException, ValidationException;

    List<Transaction> getAllTransactions(User user) throws ServiceException, ValidationException;

    List<Transaction> getTransactionsByType(User user, boolean isIncome) throws ServiceException, ValidationException;

    double getTotalByCategory(User user, String category, boolean isIncome) throws ServiceException, ValidationException;

    void transfer(User sender, User receiver, double amount, String comment) throws ServiceException, ValidationException;
}