package com.ga.csv_project.processor;

import com.ga.csv_project.model.Employee;
import com.ga.csv_project.service.SalaryCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

/**
 * Processor class that handles concurrent processing of employee salary updates.
 * Uses ExecutorService for thread pooling and CountDownLatch for synchronization.
 */
@Component
public class EmployeeProcessor {

    private final SalaryCalculatorService salaryCalculatorService;

    // Thread pool size - using fixed thread pool
    private static final int THREAD_POOL_SIZE = 4;

    @Autowired
    public EmployeeProcessor(SalaryCalculatorService salaryCalculatorService) {
        this.salaryCalculatorService = salaryCalculatorService;
    }

    /**
     * Processes all employees concurrently using a thread pool.
     * Each employee's salary is calculated and updated in a separate thread.
     *
     * @param employees list of employees to process
     */
    public void processEmployeesConcurrently(List<Employee> employees) {
        System.out.println("\n========================================");
        System.out.println("Starting concurrent salary processing...");
        System.out.println("Thread pool size: " + THREAD_POOL_SIZE);
        System.out.println("Total employees to process: " + employees.size());
        System.out.println("========================================\n");

        // Create a fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // CountDownLatch to wait for all tasks to complete
        CountDownLatch latch = new CountDownLatch(employees.size());

        long startTime = System.currentTimeMillis();

        // Submit tasks for each employee
        for (Employee employee : employees) {
            executorService.submit(() -> {
                try {
                    processEmployee(employee);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            // Wait for all tasks to complete
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Processing interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // Shutdown the executor service
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n========================================");
        System.out.println("Processing completed!");
        System.out.println("Total time: " + (endTime - startTime) + " ms");
        System.out.println("========================================\n");
    }

    /**
     * Processes a single employee - calculates and updates their salary.
     * This method is called by worker threads.
     *
     * @param employee the employee to process
     */
    private void processEmployee(Employee employee) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Processing: " + employee.getName());

        // Calculate new salary using the salary calculator service
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);

        // Update salary atomically (thread-safe)
        employee.updateSalaryAtomically(newSalary);
    }
}
