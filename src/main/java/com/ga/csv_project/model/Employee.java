package com.ga.csv_project.model;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Employee model class representing an employee record from the CSV file.
 * Uses AtomicReference for salary to ensure thread-safe updates during concurrent processing.
 */
public class Employee {

    private int id;
    private String name;
    private AtomicReference<Double> salary;
    private LocalDate joinedDate;
    private String role;
    private double projectCompletionPercentage;

    public Employee() {
        this.salary = new AtomicReference<>(0.0);
    }

    public Employee(int id, String name, double salary, LocalDate joinedDate, 
                    String role, double projectCompletionPercentage) {
        this.id = id;
        this.name = name;
        this.salary = new AtomicReference<>(salary);
        this.joinedDate = joinedDate;
        this.role = role;
        this.projectCompletionPercentage = projectCompletionPercentage;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary.get();
    }

    public void setSalary(double salary) {
        this.salary.set(salary);
    }

    // Thread-safe method to update salary atomically
    public void updateSalaryAtomically(double newSalary) {
        this.salary.set(newSalary);
    }

    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getProjectCompletionPercentage() {
        return projectCompletionPercentage;
    }

    public void setProjectCompletionPercentage(double projectCompletionPercentage) {
        this.projectCompletionPercentage = projectCompletionPercentage;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary.get() +
                ", joinedDate=" + joinedDate +
                ", role='" + role + '\'' +
                ", projectCompletionPercentage=" + projectCompletionPercentage +
                '}';
    }
}
