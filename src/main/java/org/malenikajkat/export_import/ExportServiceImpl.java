package org.malenikajkat.export_import;

import org.malenikajkat.model.Report;
import org.malenikajkat.model.Transaction;
import com.opencsv.CSVWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportServiceImpl implements ExportService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void exportCsv(Report report, String outputFile) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFile))) {
            csvWriter.writeNext(new String[]{"Тип", "Категория", "Сумма", "Дата"});

            for (Transaction tx : report.getAllTransactions()) {
                csvWriter.writeNext(new String[]{
                        tx.getType().name(),
                        tx.getCategory(),
                        String.valueOf(tx.getAmount()),
                        tx.getDate().toString()
                });
            }
        }
    }

    @Override
    public void exportJson(Report report, String outputFile) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileWriter(outputFile), report);
    }
}