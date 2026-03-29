package com.ga.csv_project.runner;

import com.ga.csv_project.model.Employee;
import com.ga.csv_project.processor.EmployeeProcessor;
import com.ga.csv_project.service.CsvReaderService;
import com.ga.csv_project.service.CsvWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CommandLineRunner that executes the CSV processing when the application starts.
 * This is the main entry point for the concurrent CSV data processor.
 */
@Component
public class CsvProcessorRunner implements CommandLineRunner {

    private final CsvReaderService csvReaderService;
    private final CsvWriterService csvWriterService;
    private final EmployeeProcessor employeeProcessor;

    // File paths
    private static final String INPUT_FILE = "src/data/test_employees.csv";
    private static final String OUTPUT_FILE = "src/data/updated_employees.csv";

    @Autowired
    public CsvProcessorRunner(CsvReaderService csvReaderService,
                              CsvWriterService csvWriterService,
                              EmployeeProcessor employeeProcessor) {
        this.csvReaderService = csvReaderService;
        this.csvWriterService = csvWriterService;
        this.employeeProcessor = employeeProcessor;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       CONCURRENT CSV DATA PROCESSOR - STARTED            ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        // Step 1: Read employees from CSV file
        System.out.println("Step 1: Reading employee data from CSV...");
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(INPUT_FILE);

        if (employees.isEmpty()) {
            System.err.println("No employees found in the CSV file. Exiting.");
            return;
        }

        // Print original salaries
        System.out.println("\n--- Original Employee Salaries ---");
        for (Employee emp : employees) {
            System.out.println(emp.getName() + ": $" + String.format("%.2f", emp.getSalary()));
        }

        // Step 2: Process employees concurrently
        System.out.println("\nStep 2: Processing salary increments concurrently...");
        employeeProcessor.processEmployeesConcurrently(employees);

        // Step 3: Write updated data to output CSV
        System.out.println("Step 3: Writing updated data to CSV...");
        csvWriterService.writeEmployeesToCsv(employees, OUTPUT_FILE);

        // Print updated salaries
        System.out.println("\n--- Updated Employee Salaries ---");
        for (Employee emp : employees) {
            System.out.println(emp.getName() + ": $" + String.format("%.2f", emp.getSalary()));
        }

        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║       CONCURRENT CSV DATA PROCESSOR - COMPLETED          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("\n");
        System.out.println("Output file saved to: " + OUTPUT_FILE);
    }
}
