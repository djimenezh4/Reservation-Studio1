package com.reservationstudio.util;

import com.reservationstudio.model.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {

    private static final String CSV_HEADER = "name,time,guests,tableNumber,status,notes";
    private final Path filePath;

    public CsvManager(String fileName) {
        // Store CSV next to the JAR / in working directory
        this.filePath = Paths.get(System.getProperty("user.dir"), fileName);
    }

   //Load all reservations from CSV, Create file with header if missing
    public ObservableList<Reservation> loadAll() {
        ObservableList<Reservation> list = FXCollections.observableArrayList();
        if (!Files.exists(filePath)) {
            createEmpty();
            return list;
        }
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                line = line.trim();
                if (line.isEmpty()) continue;
                Reservation r = Reservation.fromCsv(line);
                if (r != null) list.add(r);
            }
        } catch (IOException e) {
            System.err.println("CSV read error: " + e.getMessage());
        }
        return list;
    }

// Overwrite the CSV with the given list
    public void saveAll(List<Reservation> reservations) {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            bw.write(CSV_HEADER);
            bw.newLine();
            for (Reservation r : reservations) {
                bw.write(r.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("CSV write error: " + e.getMessage());
        }
    }

// Append a single reservation to the CSV
    public void append(Reservation r) {
        boolean needsHeader = !Files.exists(filePath);
        try (BufferedWriter bw = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (needsHeader) { bw.write(CSV_HEADER); bw.newLine(); }
            bw.write(r.toCsv());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("CSV append error: " + e.getMessage());
        }
    }

    public Path getFilePath() { return filePath; }

    private void createEmpty() {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            bw.write(CSV_HEADER);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("CSV create error: " + e.getMessage());
        }
    }
}
