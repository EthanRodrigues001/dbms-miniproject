package com.employee.gui;

import com.employee.dao.EmployeeDAO;
import com.employee.model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class EmployeeManagementGUI extends JFrame {

    private JTextField nameField, emailField, departmentField, salaryField, joinDateField;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    public EmployeeManagementGUI() {
        setTitle("Employee Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        inputPanel.add(departmentField);

        inputPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        inputPanel.add(salaryField);

        inputPanel.add(new JLabel("Join Date (YYYY-MM-DD):"));
        joinDateField = new JTextField();
        inputPanel.add(joinDateField);

        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));

        
        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(new AddEmployeeListener());
        buttonPanel.add(addButton);

        
        JButton viewButton = new JButton("View All Employees");
        viewButton.addActionListener(new ViewEmployeesListener());
        buttonPanel.add(viewButton);

        
        JButton editButton = new JButton("Edit Selected Employee");
        editButton.addActionListener(new EditEmployeeListener());
        buttonPanel.add(editButton);

        
        JButton deleteButton = new JButton("Delete Selected Employee");
        deleteButton.addActionListener(new DeleteEmployeeListener());
        buttonPanel.add(deleteButton);

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department", "Salary", "Join Date"}, 0);
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Ensure only one row can be selected
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);
    }

    
    private class AddEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String department = departmentField.getText().trim();
                String salaryText = salaryField.getText().trim();
                String joinDate = joinDateField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || department.isEmpty() || salaryText.isEmpty() || joinDate.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled out.");
                    return;
                }

                double salary = Double.parseDouble(salaryText);

                Employee employee = new Employee(name, email, department, salary, joinDate);

                EmployeeDAO employeeDAO = new EmployeeDAO();
                employeeDAO.addEmployee(employee);
                JOptionPane.showMessageDialog(null, "Employee added successfully!");
                clearFields();
                updateEmployeeTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid salary format. Please enter a valid number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error adding employee: " + ex.getMessage());
            }
        }
    }

    
    private class ViewEmployeesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateEmployeeTable();
        }
    }

   
    private class EditEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select an employee to edit.");
                return;
            }

            try {
                
                int id = (int) employeeTable.getValueAt(selectedRow, 0); 
                String name = (String) employeeTable.getValueAt(selectedRow, 1);
                String email = (String) employeeTable.getValueAt(selectedRow, 2);
                String department = (String) employeeTable.getValueAt(selectedRow, 3);
                String salaryText = employeeTable.getValueAt(selectedRow, 4).toString();
                String joinDate = (String) employeeTable.getValueAt(selectedRow, 5);

                
                nameField.setText(name);
                emailField.setText(email);
                departmentField.setText(department);
                salaryField.setText(salaryText);
                joinDateField.setText(joinDate);

                
                int response = JOptionPane.showConfirmDialog(null, "Do you want to update this employee?", "Confirm Update", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    double salary = Double.parseDouble(salaryText);

                    
                    Employee employee = new Employee(name, email, department, salary, joinDate);
                    employee.setId(id);

                    
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    employeeDAO.updateEmployee(employee);
                    JOptionPane.showMessageDialog(null, "Employee updated successfully!");
                    clearFields();
                    updateEmployeeTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid salary format. Please enter a valid number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error updating employee: " + ex.getMessage());
            }
        }
    }


    private class DeleteEmployeeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "Please select an employee to delete.");
                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);

            try {
                EmployeeDAO employeeDAO = new EmployeeDAO();
                employeeDAO.deleteEmployee(id);
                JOptionPane.showMessageDialog(null, "Employee deleted successfully!");
                updateEmployeeTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting employee: " + ex.getMessage());
            }
        }
    }


    private void updateEmployeeTable() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            List<Employee> employees = employeeDAO.getAllEmployees();

            tableModel.setRowCount(0);


            for (Employee emp : employees) {
                tableModel.addRow(new Object[]{
                    emp.getId(),
                    emp.getName(),
                    emp.getEmail(),
                    emp.getDepartment(),
                    emp.getSalary(),
                    emp.getJoinDate()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching employees: " + ex.getMessage());
        }
    }


    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        departmentField.setText("");
        salaryField.setText("");
        joinDateField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmployeeManagementGUI gui = new EmployeeManagementGUI();
            gui.setVisible(true);
        });
    }
}
