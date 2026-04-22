package com.reservationstudio;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CsvService {

    private static final String CSV_HEADER = "name,time,guests,tableNumber,status";
    private final Path filePath;

    public CsvService(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    // Load all reservations from the CSV file, creates  file if it doesn't exist
    public List<Reservation> loadAll() {
        List<Reservation> list = new ArrayList<>();
        if (!Files.exists(filePath)) {
            // Create file with header
            try {
                Files.writeString(filePath, CSV_HEADER + System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                line = line.trim();
                if (line.isEmpty()) continue;
                Reservation r = Reservation.fromCsvRow(line);
                if (r != null) list.add(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Save the full list to the CSV file. */
    public void saveAll(List<Reservation> reservations) {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(filePath))) {
            pw.println(CSV_HEADER);
            for (Reservation r : reservations) {
                pw.println(r.toCsvRow());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Append single reservation to CSV
    public void append(Reservation r) {
        // If file doesn't exist yet, write header first
        boolean needsHeader = !Files.exists(filePath);
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath.toFile(), true))) {
            if (needsHeader) pw.println(CSV_HEADER);
            pw.println(r.toCsvRow());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getFilePath() { return filePath; }
}
