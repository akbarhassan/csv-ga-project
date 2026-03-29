package com.ga.csv_project.controller;

import com.ga.csv_project.model.Employee;
import com.ga.csv_project.processor.EmployeeProcessor;
import com.ga.csv_project.service.CsvReaderService;
import com.ga.csv_project.service.CsvWriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for employee salary processing operations.
 * Provides endpoints to trigger CSV processing and view results.
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final CsvReaderService csvReaderService;
    private final CsvWriterService csvWriterService;
    private final EmployeeProcessor employeeProcessor;

    private static final String INPUT_FILE = "src/data/test_employees.csv";
    private static final String OUTPUT_FILE = "src/data/updated_employees.csv";

    @Autowired
    public EmployeeController(CsvReaderService csvReaderService,
                              CsvWriterService csvWriterService,
                              EmployeeProcessor employeeProcessor) {
        this.csvReaderService = csvReaderService;
        this.csvWriterService = csvWriterService;
        this.employeeProcessor = employeeProcessor;
    }

    /**
     * GET /api/employees
     * Returns all employees from the input CSV file.
     */
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(INPUT_FILE);
        return ResponseEntity.ok(employees);
    }

    /**
     * POST /api/employees/process
     * Processes all employee salaries concurrently and saves to output file.
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processEmployees() {
        Map<String, Object> response = new HashMap<>();

        // Read employees
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(INPUT_FILE);

        if (employees.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No employees found in CSV file");
            return ResponseEntity.badRequest().body(response);
        }

        // Store original salaries for comparison
        Map<String, Double> originalSalaries = new HashMap<>();
        for (Employee emp : employees) {
            originalSalaries.put(emp.getName(), emp.getSalary());
        }

        // Process concurrently
        employeeProcessor.processEmployeesConcurrently(employees);

        // Write to output file
        csvWriterService.writeEmployeesToCsv(employees, OUTPUT_FILE);

        // Build response with salary changes
        response.put("status", "success");
        response.put("message", "Processed " + employees.size() + " employees");
        response.put("outputFile", OUTPUT_FILE);
        response.put("employees", employees);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/employees/updated
     * Returns all employees from the output CSV file (after processing).
     */
    @GetMapping("/updated")
    public ResponseEntity<List<Employee>> getUpdatedEmployees() {
        List<Employee> employees = csvReaderService.readEmployeesFromCsv(OUTPUT_FILE);
        
        if (employees.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(employees);
    }
}
