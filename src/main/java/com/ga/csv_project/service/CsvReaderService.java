package com.ga.csv_project.service;

import com.ga.csv_project.model.Employee;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service class responsible for reading employee data from CSV files.
 * Uses ReentrantLock to ensure thread-safe file reading operations.
 */
@Service
public class CsvReaderService {

    private final ReentrantLock fileLock = new ReentrantLock();

    /**
     * Reads employee data from a CSV file.
     * The file format expected: id, name, salary, joinedDate, role, projectCompletionPercentage
     *
     * @param filePath path to the CSV file
     * @return list of Employee objects
     */
    public List<Employee> readEmployeesFromCsv(String filePath) {
        List<Employee> employees = new ArrayList<>();

        // Acquire lock before reading file to ensure thread safety
        fileLock.lock();
        try {
            System.out.println("Reading CSV file: " + filePath);
            
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                List<String[]> records = reader.readAll();

                for (String[] record : records) {
                    Employee employee = parseEmployee(record);
                    if (employee != null) {
                        employees.add(employee);
                    }
                }

                System.out.println("Successfully read " + employees.size() + " employees from CSV");

            } catch (IOException | CsvException e) {
                System.err.println("Error reading CSV file: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            // Always release the lock
            fileLock.unlock();
        }

        return employees;
    }

    /**
     * Parses a CSV record into an Employee object.
     *
     * @param record array of strings representing a CSV row
     * @return Employee object or null if parsing fails
     */
    private Employee parseEmployee(String[] record) {
        try {
            if (record.length < 6) {
                System.err.println("Invalid record length: " + record.length);
                return null;
            }

            int id = Integer.parseInt(record[0].trim());
            String name = record[1].trim();
            double salary = Double.parseDouble(record[2].trim());
            LocalDate joinedDate = LocalDate.parse(record[3].trim());
            String role = record[4].trim();
            double projectCompletion = Double.parseDouble(record[5].trim());

            return new Employee(id, name, salary, joinedDate, role, projectCompletion);

        } catch (Exception e) {
            System.err.println("Error parsing employee record: " + e.getMessage());
            return null;
        }
    }
}
