package com.ga.csv_project.service;

import com.ga.csv_project.model.Employee;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Service class responsible for writing employee data to CSV files.
 * Uses Semaphore to control concurrent access to file writing operations.
 */
@Service
public class CsvWriterService {

    // Semaphore with 1 permit to ensure only one thread writes at a time
    private final Semaphore writeSemaphore = new Semaphore(1);

    /**
     * Writes the list of employees to a CSV file.
     *
     * @param employees list of employees to write
     * @param outputPath path to the output CSV file
     */
    public void writeEmployeesToCsv(List<Employee> employees, String outputPath) {
        try {
            // Acquire semaphore before writing
            writeSemaphore.acquire();
            System.out.println("Writing updated employee data to: " + outputPath);

            try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END)) {

                for (Employee employee : employees) {
                    String[] record = {
                            String.valueOf(employee.getId()),
                            employee.getName(),
                            String.format("%.2f", employee.getSalary()),
                            employee.getJoinedDate().toString(),
                            employee.getRole(),
                            String.valueOf(employee.getProjectCompletionPercentage())
                    };
                    writer.writeNext(record);
                }

                System.out.println("Successfully wrote " + employees.size() + " employees to CSV");

            } catch (IOException e) {
                System.err.println("Error writing CSV file: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (InterruptedException e) {
            System.err.println("Thread interrupted while waiting for write semaphore");
            Thread.currentThread().interrupt();
        } finally {
            // Release semaphore
            writeSemaphore.release();
        }
    }
}
