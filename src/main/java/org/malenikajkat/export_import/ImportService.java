package org.malenikajkat.export_import;

import org.malenikajkat.model.Report;

import java.io.IOException;

public interface ImportService {
    Report importCsv(String inputFile) throws IOException;
    Report importJson(String inputFile) throws IOException;
}