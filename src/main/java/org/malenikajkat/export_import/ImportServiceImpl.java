package org.malenikajkat.export_import;

import org.malenikajkat.model.Budget;
import org.malenikajkat.model.Report;
import org.malenikajkat.model.Transaction;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ImportServiceImpl implements ImportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Report importCsv(String inputFile) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(inputFile))
                .withCSVParser(new CSVParserBuilder().build())
                .build()) {
            csvReader.skip(1);

            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                String typeStr = nextRecord[0];
                String category = nextRecord[1];
                double amount = Double.parseDouble(nextRecord[2]);
                LocalDate date = LocalDate.parse(nextRecord[3]);

                Transaction.Type txType = Transaction.Type.valueOf(typeStr);
                transactions.add(new Transaction(amount, "", category, txType, date));
            }
        } catch (com.opencsv.exceptions.CsvValidationException e) {
            throw new IOException("Ошибка парсинга CSV: " + e.getMessage(), e);
        }

        Map<String, Double> incomeByCategory = new HashMap<>();
        Map<String, Double> expenseByCategory = new HashMap<>();
        List<Budget> budgets = Collections.emptyList();
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.Type.INCOME) {
                incomeByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                totalIncome += transaction.getAmount();
            } else {
                expenseByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                totalExpense += transaction.getAmount();
            }
        }

        return new Report(incomeByCategory, totalIncome, expenseByCategory, totalExpense, budgets, transactions);
    }

    @Override
    public Report importJson(String inputFile) throws IOException {
        return objectMapper.readValue(new FileReader(inputFile), Report.class);
    }
}