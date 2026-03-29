package com.ga.csv_project.service;

import com.ga.csv_project.model.Employee;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service class responsible for calculating salary increments based on business rules.
 * 
 * Business Rules:
 * - If project completion < 60%, no salary increase
 * - 2% increase for each completed year of work
 * - Role-based increase: Director 5%, Manager 2%, Employee 1%
 */
@Service
public class SalaryCalculatorService {

    private static final double PROJECT_COMPLETION_THRESHOLD = 0.60;
    private static final double YEARLY_INCREMENT_PERCENTAGE = 0.02;
    private static final double DIRECTOR_INCREMENT = 0.05;
    private static final double MANAGER_INCREMENT = 0.02;
    private static final double EMPLOYEE_INCREMENT = 0.01;

    /**
     * Calculates the new salary for an employee based on business rules.
     *
     * @param employee the employee to calculate salary for
     * @return the new salary after applying increments
     */
    public double calculateNewSalary(Employee employee) {
        double currentSalary = employee.getSalary();
        double totalIncrementPercentage = 0.0;

        // Check if employee completed at least 60% of projects
        if (employee.getProjectCompletionPercentage() < PROJECT_COMPLETION_THRESHOLD) {
            System.out.println(employee.getName() + " - No increment (project completion below 60%)");
            return currentSalary;
        }

        // Calculate years worked increment
        int yearsWorked = calculateCompletedYears(employee.getJoinedDate());
        double yearsIncrement = yearsWorked * YEARLY_INCREMENT_PERCENTAGE;
        totalIncrementPercentage += yearsIncrement;

        // Calculate role-based increment
        double roleIncrement = getRoleIncrement(employee.getRole());
        totalIncrementPercentage += roleIncrement;

        // Calculate new salary
        double newSalary = currentSalary * (1 + totalIncrementPercentage);

        System.out.println(employee.getName() + " - Years: " + yearsWorked + 
                          ", Role: " + employee.getRole() + 
                          ", Total Increment: " + (totalIncrementPercentage * 100) + "%" +
                          ", Old Salary: " + currentSalary + 
                          ", New Salary: " + String.format("%.2f", newSalary));

        return newSalary;
    }

    /**
     * Calculates the number of completed years since the joined date.
     *
     * @param joinedDate the date the employee joined
     * @return number of completed years
     */
    private int calculateCompletedYears(LocalDate joinedDate) {
        LocalDate today = LocalDate.now();
        long years = ChronoUnit.YEARS.between(joinedDate, today);
        return (int) years;
    }

    /**
     * Gets the increment percentage based on employee role.
     *
     * @param role the employee's role
     * @return increment percentage as decimal
     */
    private double getRoleIncrement(String role) {
        switch (role.toLowerCase()) {
            case "director":
                return DIRECTOR_INCREMENT;
            case "manager":
                return MANAGER_INCREMENT;
            case "employee":
                return EMPLOYEE_INCREMENT;
            default:
                System.err.println("Unknown role: " + role + ", applying 0% increment");
                return 0.0;
        }
    }
}
