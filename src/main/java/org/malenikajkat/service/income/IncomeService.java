package org.malenikajkat.service.income;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;

import java.util.List;

public interface IncomeService {

    void addIncome(User user, double amount, String category) throws ServiceException;

    List<Transaction> getAllIncomes(User user) throws ServiceException;

    List<Transaction> getIncomesByCategory(User user, String category) throws ServiceException;

    double getTotalIncome(User user) throws ServiceException;

    double getTotalIncomeByCategory(User user, String category) throws ServiceException;

    boolean hasIncomeInCategory(User user, String category) throws ServiceException;
}
