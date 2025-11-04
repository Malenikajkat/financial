package org.malenikajkat.service.income;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Transaction;
import org.malenikajkat.exception.ServiceException;
import org.malenikajkat.exception.ValidationException;

import java.util.List;

public interface IncomeService {

    void addIncome(User user, double amount, String category) throws ServiceException, ValidationException;

    List<Transaction> getAllIncomes(User user) throws ServiceException, ValidationException;

    List<Transaction> getIncomesByCategory(User user, String category) throws ServiceException, ValidationException;

    double getTotalIncome(User user) throws ServiceException, ValidationException;

    double getTotalIncomeByCategory(User user, String category) throws ServiceException, ValidationException;

    boolean hasIncomeInCategory(User user, String category) throws ServiceException, ValidationException;
}