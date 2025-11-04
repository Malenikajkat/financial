package org.malenikajkat.export_import;

import org.malenikajkat.model.Report;

import java.io.IOException;

public interface ExportService {
    void exportCsv(Report report, String outputFile) throws IOException;
    void exportJson(Report report, String outputFile) throws IOException;
}