package org.example.miniproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    // ChoiceBox for selecting query type
    @FXML
    private ChoiceBox<String> queryBox;

    private String[] queryType = {"SELECT", "UPDATE", "DELETE"};

    @FXML
    private TableView<User> table;
    @FXML
    private TableColumn<User, Integer> colUserId;
    @FXML
    private TableColumn<User, String> colFirstName;
    @FXML
    private TableColumn<User, String> colLastName;
    @FXML
    private TableColumn<User, String> colAddress;
    @FXML
    private TableColumn<User, String> colZip;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colPassword;

    @FXML
    private TextField queryField;

    @FXML
    private Connection connection = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        queryBox.getItems().addAll(queryType);

        colUserId.setCellValueFactory(new PropertyValueFactory<User, Integer>("userId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<User, String>("address"));
        colZip.setCellValueFactory(new PropertyValueFactory<User, String>("zip"));
        colUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
    }

    public void onTextFieldEnter() {
        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:mysql://cis3270airlinedatabase.mysql.database.azure.com/database", "username", "Password!");

            // Get the selected query type (SELECT, UPDATE, DELETE)
            String selectedQuery = queryBox.getValue();

            // Get the user input
            String userInput = queryField.getText().trim();

            // Initialize query variable
            String query = "";

            if ("SELECT".equals(selectedQuery)) {
                // Handling SELECT query
                if (userInput.isEmpty()) {
                    query = "SELECT * FROM miniproject";  // Default to selecting all columns
                } else if (userInput.toLowerCase().startsWith("where")) {
                    query = "SELECT * FROM miniproject " + userInput;  // If it's a WHERE condition
                } else {
                    query = "SELECT " + userInput + " FROM miniproject";  // Otherwise, it's a list of columns
                }

            } else if ("UPDATE".equals(selectedQuery)) {
                // Handling UPDATE query
                if (userInput.isEmpty()) {
                    showAlert("Invalid Input", "You must specify what to update.");
                    return;
                }
                query = "UPDATE miniproject SET " + userInput;  // User input should be in form of column=value pairs (e.g., "first_name='John' WHERE user_id=5")

            } else if ("DELETE".equals(selectedQuery)) {
                // Handling DELETE query
                if (userInput.isEmpty()) {
                    showAlert("Invalid Input", "You must specify the condition for deletion.");
                    return;
                }
                query = "DELETE FROM miniproject " + userInput;  // User input should be a WHERE condition (e.g., "WHERE user_id=5")
            }

            // Print the query for debugging
            System.out.println("Generated Query: " + query);

            // Prepare and execute the query
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Execute the query based on its type
            if ("SELECT".equals(selectedQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                ObservableList<User> users = loadUsersFromResultSet(resultSet);
                table.setItems(users);
            } else {
                int rowsAffected = preparedStatement.executeUpdate();
                System.out.println(rowsAffected + " rows affected.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("SQL Error", "An error occurred while executing the query.");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to load users from ResultSet
    private ObservableList<User> loadUsersFromResultSet(ResultSet resultSet) throws SQLException {
        ObservableList<User> users = FXCollections.observableArrayList();

        while (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String address = resultSet.getString("address");
            String zip = resultSet.getString("zip");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");

            User user = new User(userId, firstName, lastName, address, zip, username, password);
            users.add(user);
        }

        return users;
    }

    // Helper method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}