package src.employee.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class Home extends JFrame implements ActionListener {
    JButton addButton, deleteButton, updateButton, refreshTableButton, logoutButton;
    JComboBox<String> departmentComboBox;
    JTextField nameField, joinDateField, birthDateField;
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
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        addEmployeePanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(nameField, gbc);

        // Join Date field
        gbc.gridx = 0;
        gbc.gridy = 1;
        addEmployeePanel.add(new JLabel("Join Date (YYYY-MM-DD):"), gbc);
        joinDateField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(joinDateField, gbc);

        // Birth Date field
        gbc.gridx = 0;
        gbc.gridy = 2;
        addEmployeePanel.add(new JLabel("Birth Date (YYYY-MM-DD):"), gbc);
        birthDateField = new JTextField(10);
        gbc.gridx = 1;
        addEmployeePanel.add(birthDateField, gbc);
        
        // Department field
        gbc.gridx = 0;
        gbc.gridy = 3;
        addEmployeePanel.add(new JLabel("Department:"), gbc);
        departmentComboBox = new JComboBox<>(new String[]{"HR", "Sales", "IT", "Finance"});
        gbc.gridx = 1;
        addEmployeePanel.add(departmentComboBox, gbc);
        
        // Add Button
        addButton = new JButton("Add Employee");
        addButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 4;
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
                selectedEmployeeId = Integer.parseInt(employeeTable.getValueAt(row, 0).toString());
                nameField.setText(employeeTable.getValueAt(row, 1).toString());
                joinDateField.setText(employeeTable.getValueAt(row, 3).toString());
                birthDateField.setText(employeeTable.getValueAt(row, 4).toString());
                departmentComboBox.setSelectedItem(employeeTable.getValueAt(row, 2).toString());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE); // Match background color

        // Update Button
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
            new Login();  // Redirect to login page
        });
        buttonPanel.add(logoutButton);

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

            // Pre-fill the fields with selected employee data
            nameFieldUpdate.setText(nameField.getText());
            joinDateFieldUpdate.setText(joinDateField.getText());
            birthDateFieldUpdate.setText(birthDateField.getText());
            departmentComboBoxUpdate.setSelectedItem(departmentComboBox.getSelectedItem());

            JPanel updatePanel = new JPanel(new GridLayout(0, 2));
            updatePanel.add(new JLabel("Name:"));
            updatePanel.add(nameFieldUpdate);
            updatePanel.add(new JLabel("Join Date:"));
            updatePanel.add(joinDateFieldUpdate);
            updatePanel.add(new JLabel("Birth Date:"));
            updatePanel.add(birthDateFieldUpdate);
            updatePanel.add(new JLabel("Department:"));
            updatePanel.add(departmentComboBoxUpdate);

            int result = JOptionPane.showConfirmDialog(null, updatePanel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                updateEmployee(selectedEmployeeId, nameFieldUpdate.getText(), joinDateFieldUpdate.getText(),
                        birthDateFieldUpdate.getText(), (String) departmentComboBoxUpdate.getSelectedItem());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an employee to update");
        }
    }

    private void updateEmployee(int id, String name, String joinDate, String birthDate, String department) {
        try {
            Conn c = new Conn();
            String query = "UPDATE employees SET name = ?, department = ?, join_date = ?, birth_date = ? WHERE id = ?";
            PreparedStatement stmt = c.getConnection().prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, department);
            stmt.setString(3, joinDate);
            stmt.setString(4, birthDate);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Employee Updated Successfully");
            showEmployees();  // Refresh the table
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
        String query = "SELECT id, name, department, join_date, birth_date FROM employees";
        ResultSet rs = c.getStatement().executeQuery(query);
        
        if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
            JOptionPane.showMessageDialog(null, "No employees found.");
            employeeTable.setModel(new DefaultTableModel()); // Clear table
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

        try {
            Conn c = new Conn();
            
            // Get the highest existing ID
            String query = "SELECT MAX(id) AS max_id FROM employees";
            ResultSet rs = c.getStatement().executeQuery(query);
            int newId = 1; // Default ID if the table is empty
            if (rs.next()) {
                newId = rs.getInt("max_id") + 1; // Increment for the new ID
            }

            // Now insert the new employee with the manually assigned ID
            String insertQuery = "INSERT INTO employees (id, name, department, join_date, birth_date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = c.getConnection().prepareStatement(insertQuery);
            stmt.setInt(1, newId);
            stmt.setString(2, name);
            stmt.setString(3, department);
            stmt.setString(4, joinDate);
            stmt.setString(5, birthDate);
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Employee Added Successfully");
            showEmployees();  // Refresh the table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEmployee() {
        if (selectedEmployeeId != -1) {
            try {
                Conn c = new Conn();
                String query = "DELETE FROM employees WHERE id = ?";
                PreparedStatement stmt = c.getConnection().prepareStatement(query);
                stmt.setInt(1, selectedEmployeeId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Employee Deleted Successfully");
                showEmployees();  // Refresh the table
                selectedEmployeeId = -1;  // Reset selected employee ID
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select an employee to delete");
        }
    }

    private static TableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
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
