import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboar {
    JFrame Frame = new JFrame();
    private JTable userTable;
    private JTable customerTable;
    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;
    private DefaultTableModel userTableModel;
    private DefaultTableModel customerTableModel;

    void AdminDashboard (){
        Frame.setTitle("Admin Dashboard");
        Frame.setSize(900, 600);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Manage Users", createUserPanel());
        tabbedPane.addTab("Manage Customers", createCustomerPanel());

        Frame.add(tabbedPane, BorderLayout.CENTER);

        // Load the data
        loadUsers();
        loadCustomers();
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // User table model
        userTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role"}, 0);
        userTable = new JTable(userTableModel);

        JScrollPane tableScrollPane = new JScrollPane(userTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addUserButton = new JButton("Add User");
        JButton editUserButton = new JButton("Edit User");
        JButton deleteUserButton = new JButton("Delete User");

        addUserButton.addActionListener(e -> addUser());
        editUserButton.addActionListener(e -> editUser());
        deleteUserButton.addActionListener(e -> deleteUser());

        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchCustomers());

        statusFilterComboBox = new JComboBox<>(new String[]{"All", "New", "Active", "Inactive", "Prospective"});
        statusFilterComboBox.addActionListener(e -> loadCustomers());

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Filter by Status:"));
        searchPanel.add(statusFilterComboBox);

        // Customer table model
        customerTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Company", "Status"}, 0);
        customerTable = new JTable(customerTableModel);

        JScrollPane tableScrollPane = new JScrollPane(customerTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addCustomerButton = new JButton("Add Customer");
        JButton editCustomerButton = new JButton("Edit Customer");
        JButton deleteCustomerButton = new JButton("Delete Customer");

        addCustomerButton.addActionListener(e -> addCustomer());
        editCustomerButton.addActionListener(e -> editCustomer());
        deleteCustomerButton.addActionListener(e -> deleteCustomer());

        buttonPanel.add(addCustomerButton);
        buttonPanel.add(editCustomerButton);
        buttonPanel.add(deleteCustomerButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUsers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, name, email, role FROM Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            userTableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                userTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Customers";
            String selectedStatus = statusFilterComboBox.getSelectedItem().toString();

            if (!selectedStatus.equals("All")) {
                sql += " WHERE status = ?";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);

            if (!selectedStatus.equals("All")) {
                pstmt.setString(1, selectedStatus);
            }
            ResultSet rs = pstmt.executeQuery();

            customerTableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                customerTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("company"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addUser() {
        // Logic to add a new user
        // Display a form to enter user details and insert the new user into the Users table
    }

    private void editUser() {
        // Logic to edit an existing user
        // Fetch selected user details, display them in a form, and update the Users table with new values
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }

        int userId = (int) userTableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM Users WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addCustomer() {
        // Logic to add a new customer
        // Display a form to enter customer details and insert the new customer into the Customers table
    }

    private void editCustomer() {
        // Logic to edit an existing customer
        // Fetch selected customer details, display them in a form, and update the Customers table with new values
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }

        int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM Customers WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer deleted successfully.");
            loadCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchCustomers() {
        String query = searchField.getText();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM Customers WHERE name LIKE ? OR company LIKE ? OR email LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            pstmt.setString(3, "%" + query + "%");

            ResultSet rs = pstmt.executeQuery();
            customerTableModel.setRowCount(0); // Clear existing data
            while (rs.next()) {
                customerTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("company"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
