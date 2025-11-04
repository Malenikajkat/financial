package org.malenikajkat.service.report;

import org.malenikajkat.model.User;
import org.malenikajkat.model.Report;
import org.malenikajkat.exception.ServiceException;

import java.time.LocalDate;

public interface ReportGenerator {
    Report generateSummaryReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException;
    Report generateCategoryReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException;
    Report generateBalanceTrendReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException;
    Report generateExpenseDistributionReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException;
    Report generateCompactReport(User user, LocalDate startDate, LocalDate endDate) throws ServiceException;
    Report generateReport(User user) throws ServiceException;
}