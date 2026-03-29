package com.ga.csv_project.service;

import com.ga.csv_project.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SalaryCalculatorService.
 * Tests the salary increment business logic.
 */
class SalaryCalculatorServiceTest {

    private SalaryCalculatorService salaryCalculatorService;

    @BeforeEach
    void setUp() {
        salaryCalculatorService = new SalaryCalculatorService();
    }

    @Test
    void testNoIncrementWhenProjectCompletionBelow60Percent() {
        // Employee with 50% project completion should not get any increment
        Employee employee = new Employee(1, "Test", 50000.0, 
                LocalDate.now().minusYears(5), "Manager", 0.5);
        
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);
        
        assertEquals(50000.0, newSalary, "Salary should remain unchanged");
    }

    @Test
    void testDirectorGets5PercentIncrement() {
        // Director with 80% completion, joined today (0 years)
        Employee employee = new Employee(1, "Test Director", 100000.0, 
                LocalDate.now(), "Director", 0.8);
        
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);
        
        // Should get 5% role increment only (0 years worked)
        assertEquals(105000.0, newSalary, 0.01, "Director should get 5% increment");
    }

    @Test
    void testManagerGets2PercentIncrement() {
        // Manager with 70% completion, joined today (0 years)
        Employee employee = new Employee(1, "Test Manager", 100000.0, 
                LocalDate.now(), "Manager", 0.7);
        
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);
        
        // Should get 2% role increment only
        assertEquals(102000.0, newSalary, 0.01, "Manager should get 2% increment");
    }

    @Test
    void testEmployeeGets1PercentIncrement() {
        // Employee with 60% completion, joined today (0 years)
        Employee employee = new Employee(1, "Test Employee", 100000.0, 
                LocalDate.now(), "Employee", 0.6);
        
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);
        
        // Should get 1% role increment only
        assertEquals(101000.0, newSalary, 0.01, "Employee should get 1% increment");
    }

    @Test
    void testYearsWorkedIncrement() {
        // Employee with 3 years of work should get 6% years increment + 1% role
        Employee employee = new Employee(1, "Test", 100000.0, 
                LocalDate.now().minusYears(3), "Employee", 0.8);
        
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);
        
        // 3 years * 2% = 6% + 1% role = 7% total
        assertEquals(107000.0, newSalary, 0.01, "Should get 7% total increment");
    }

    @Test
    void testCombinedIncrements() {
        // Director with 5 years of work
        Employee employee = new Employee(1, "Senior Director", 100000.0, 
                LocalDate.now().minusYears(5), "Director", 0.9);
        
        double newSalary = salaryCalculatorService.calculateNewSalary(employee);
        
        // 5 years * 2% = 10% + 5% director = 15% total
        assertEquals(115000.0, newSalary, 0.01, "Should get 15% total increment");
    }
}
