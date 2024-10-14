package src.employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

public class Home extends JFrame implements ActionListener {
    JButton addButton, deleteButton, updateButton, refreshTableButton, logoutButton, generateCsvButton;
    JComboBox<String> departmentComboBox;
    JTextField nameField, joinDateField, birthDateField, salaryField;
    JTable employeeTable;
    int selectedEmployeeId = -1;

    public Home() {
        setLayout(new BorderLayout());
        setTitle("Employee Management System");
        setSize(800, 600);

        // Styling
        UIManager.put("Button.background", Color.BLACK);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("TextField.background", Color.LIGHT_GRAY);
        UIManager.put("TextField.foreground", Color.BLACK);
        
        JPanel addEmployeePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        addEmployeePanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addEmployeePanel.add(new JLabel("Join Date (YYYY-MM-DD):"), gbc);
        joinDateField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(joinDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addEmployeePanel.add(new JLabel("Birth Date (YYYY-MM-DD):"), gbc);
        birthDateField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(birthDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        addEmployeePanel.add(new JLabel("Department:"), gbc);
        departmentComboBox = new JComboBox<>(new String[]{"HR", "Sales", "IT", "Finance"});
        gbc.gridx = 1;
        addEmployeePanel.add(departmentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addEmployeePanel.add(new JLabel("Salary:"), gbc);
        salaryField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(salaryField, gbc);

        addButton = new JButton("Add Employee");
        addButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 5;
        addEmployeePanel.add(addButton, gbc);

        add(addEmployeePanel, BorderLayout.NORTH);

        employeeTable = new JTable();
        employeeTable.setFillsViewportHeight(true);
        employeeTable.setBackground(Color.LIGHT_GRAY);
        employeeTable.setForeground(Color.BLACK);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = employeeTable.getSelectedRow();
                if (row != -1) { // Check if a valid row is selected
                    selectedEmployeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());
                    nameField.setText(employeeTable.getValueAt(row, 1).toString());
                    joinDateField.setText(employeeTable.getValueAt(row, 3).toString());
                    birthDateField.setText(employeeTable.getValueAt(row, 4).toString());
                    departmentComboBox.setSelectedItem(employeeTable.getValueAt(row, 2).toString());
                    salaryField.setText(employeeTable.getValueAt(row, 5).toString());
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a valid row.");
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        updateButton = new JButton("Update Employee");
        updateButton.addActionListener(e -> showUpdateEmployeeDialog());
        buttonPanel.add(updateButton);

        deleteButton = new JButton("Delete Employee");
        deleteButton.addActionListener(this);
        buttonPanel.add(deleteButton);

        refreshTableButton = new JButton("Refresh Table");
        refreshTableButton.addActionListener(this);
        buttonPanel.add(refreshTableButton);
        
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            setVisible(false);
            new Login(); 
        });
        buttonPanel.add(logoutButton);

        // Generate CSV Button
        generateCsvButton = new JButton("Generate CSV");
        generateCsvButton.addActionListener(e -> generateCsvFile());
        buttonPanel.add(generateCsvButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void showUpdateEmployeeDialog() {
        if (selectedEmployeeId != -1) {
            JTextField nameFieldUpdate = new JTextField(10);
            JTextField joinDateFieldUpdate = new JTextField(10);
            JTextField birthDateFieldUpdate = new JTextField(10);
            JComboBox<String> departmentComboBoxUpdate = new JComboBox<>(new String[]{"HR", "Sales", "IT", "Finance"});
            JTextField salaryFieldUpdate = new JTextField(10);

            nameFieldUpdate.setText(nameField.getText());
            joinDateFieldUpdate.setText(joinDateField.getText());
            birthDateFieldUpdate.setText(birthDateField.getText());
            departmentComboBoxUpdate.setSelectedItem(departmentComboBox.getSelectedItem());
            salaryFieldUpdate.setText(salaryField.getText());

            JPanel updatePanel = new JPanel(new GridLayout(0, 2));
            updatePanel.add(new JLabel("Name:"));
            updatePanel.add(nameFieldUpdate);
            updatePanel.add(new JLabel("Join Date:"));
            updatePanel.add(joinDateFieldUpdate);
            updatePanel.add(new JLabel("Birth Date:"));
            updatePanel.add(birthDateFieldUpdate);
            updatePanel.add(new JLabel("Department:"));
            updatePanel.add(departmentComboBoxUpdate);
            updatePanel.add(new JLabel("Salary:"));
            updatePanel.add(salaryFieldUpdate);

            int result = JOptionPane.showConfirmDialog(null, updatePanel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                updateEmployee(selectedEmployeeId, nameFieldUpdate.getText(), joinDateFieldUpdate.getText(),
                        birthDateFieldUpdate.getText(), (String) departmentComboBoxUpdate.getSelectedItem(), salaryFieldUpdate.getText());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an employee to update");
        }
    }

    private void updateEmployee(int id, String name, String joinDate, String birthDate, String department, String salary) {
        try {
            Conn c = new Conn();
            String query = "UPDATE employees SET name = ?, department = ?, join_date = ?, birth_date = ?, salary = ? WHERE id = ?";
            PreparedStatement stmt = c.getConnection().prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, department);
            stmt.setString(3, joinDate);
            stmt.setString(4, birthDate);
            stmt.setDouble(5, Double.parseDouble(salary));  // Set salary
            stmt.setInt(6, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Employee Updated Successfully");
            showEmployees(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == refreshTableButton) {
            showEmployees();
        } else if (ae.getSource() == addButton) {
            addEmployee();
        } else if (ae.getSource() == deleteButton) {
            deleteEmployee();
        }
    }

    private void showEmployees() {
        try {
            Conn c = new Conn();
            String query = "SELECT id, name, department, join_date, birth_date, salary FROM employees";
            ResultSet rs = c.getStatement().executeQuery(query);
            
            if (!rs.isBeforeFirst()) { 
                JOptionPane.showMessageDialog(null, "No employees found.");
                employeeTable.setModel(new DefaultTableModel());
                return;
            }
            
            employeeTable.setModel(buildTableModel(rs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        String name = nameField.getText();
        String joinDate = joinDateField.getText();
        String birthDate = birthDateField.getText();
        String department = (String) departmentComboBox.getSelectedItem();
        String salary = salaryField.getText();

        try {
            Conn c = new Conn();
            
            // Get the highest existing ID
            String query = "SELECT MAX(id) AS max_id FROM employees";
            ResultSet rs = c.getStatement().executeQuery(query);
            int newId = 1;
            if (rs.next()) {
                newId = rs.getInt("max_id") + 1;
            }

            // Now insert the new employee with the manually assigned ID
            String insertQuery = "INSERT INTO employees (id, name, department, join_date, birth_date, salary) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = c.getConnection().prepareStatement(insertQuery);
            stmt.setInt(1, newId); 
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, joinDate);
            stmt.setString(5, birthDate);
            stmt.setDouble(6, Double.parseDouble(salary));  
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Employee Added Successfully");
            showEmployees(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEmployee() {
        if (selectedEmployeeId == -1) {
            JOptionPane.showMessageDialog(null, "Please select an employee to delete");
            return;
        }

        try {
            Conn c = new Conn();
            String query = "DELETE FROM employees WHERE id = " + selectedEmployeeId;
            c.getStatement().executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Employee Deleted Successfully");
            showEmployees(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateCsvFile() {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM employees";
            ResultSet rs = c.getStatement().executeQuery(query);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No employee data found.");
                return;
            }

            FileWriter csvWriter = new FileWriter("employees.csv");
            csvWriter.append("ID, Name, Department, Join Date, Birth Date, Salary\n");
            while (rs.next()) {
                csvWriter.append(rs.getInt("id") + ", ");
                csvWriter.append(rs.getString("name") + ", ");
                csvWriter.append(rs.getString("department") + ", ");
                csvWriter.append(rs.getString("join_date") + ", ");
                csvWriter.append(rs.getString("birth_date") + ", ");
                csvWriter.append(rs.getString("salary") + "\n");
            }
            csvWriter.flush();
            csvWriter.close();
            JOptionPane.showMessageDialog(null, "CSV file generated successfully.");
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        new Home();
    }
}
