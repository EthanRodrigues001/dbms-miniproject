package com.employee.view;

import com.employee.dao.EmployeeDAO;
import com.employee.model.Employee;


import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class EmployeeManagementSystem {

    private static EmployeeDAO employeeDAO = new EmployeeDAO();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nEmployee Management System");
            System.out.println("1. Add Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. View Employee by ID");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    viewAllEmployees();
                    break;
                case 3:
                    updateEmployee();
                    break;
                case 4:
                    deleteEmployee();
                    break;
                case 5:
                    viewEmployeeById();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void addEmployee() {
        System.out.print("Enter name: ");
        String name = scanner.next();
        System.out.print("Enter email: ");
        String email = scanner.next();
        System.out.print("Enter department: ");
        String department = scanner.next();
        System.out.print("Enter salary: ");
        double salary = scanner.nextDouble();
        System.out.print("Enter join date (YYYY-MM-DD): ");
        String joinDate = scanner.next();

        Employee employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setDepartment(department);
        employee.setSalary(salary);
        employee.setJoinDate(joinDate);

        try {
            employeeDAO.addEmployee(employee);
            System.out.println("Employee added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
        }
    }

    private static void viewAllEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            if (employees.isEmpty()) {
                System.out.println("No employees found.");
            } else {
                for (Employee employee : employees) {
                    System.out.println(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
    }

    private static void updateEmployee() {
        System.out.print("Enter the ID of the employee to update: ");
        int id = scanner.nextInt();
        try {
            Employee employee = employeeDAO.getEmployeeById(id);
            if (employee == null) {
                System.out.println("Employee not found.");
                return;
            }
            System.out.print("Enter new name: ");
            employee.setName(scanner.next());
            System.out.print("Enter new email: ");
            employee.setEmail(scanner.next());
            System.out.print("Enter new department: ");
            employee.setDepartment(scanner.next());
            System.out.print("Enter new salary: ");
            employee.setSalary(scanner.nextDouble());
            System.out.print("Enter new join date (YYYY-MM-DD): ");
            employee.setJoinDate(scanner.next());

            employeeDAO.updateEmployee(employee);
            System.out.println("Employee updated successfully.");
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
        }
    }

    private static void deleteEmployee() {
        System.out.print("Enter the ID of the employee to delete: ");
        int id = scanner.nextInt();
        try {
            employeeDAO.deleteEmployee(id);
            System.out.println("Employee deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
        }
    }

    private static void viewEmployeeById() {
        System.out.print("Enter the ID of the employee to view: ");
        int id = scanner.nextInt();
        try {
            Employee employee = employeeDAO.getEmployeeById(id);
            if (employee != null) {
                System.out.println(employee);
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee: " + e.getMessage());
        }
    }
}
