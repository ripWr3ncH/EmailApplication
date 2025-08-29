package com.example.emailapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginPageController extends AbsController implements Initializable {


    @FXML
    private  TextField emailField;

    @FXML
    private Label emailText;

    @FXML
    private Label errorLabel;

    @FXML
    private Label passText;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ContextMenu emailSuggestions;

    public LoginPageController() {}

    public LoginPageController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void loginButtonAction() {
        System.out.println("Login button clicked");
        if (fieldsarevalid()) {
            Account account = new Account(emailField.getText(), passwordField.getText());
            LoginService loginService = new LoginService(account, emailManager);
            loginService.start();

            loginService.setOnSucceeded(event -> {
                EmailLoginResult emailLoginResult = loginService.getValue();
                switch (emailLoginResult) {
                    case SUCCESS:
                        System.out.println("Login successful " + account);

                        boolean isSaved = DatabaseHelper.isAccountSaved(account.getAddress());
                        System.out.println("Is account saved? " + isSaved); // Debugging line

                        // Show "Login Successful" message after successful login
                        showInfoDialog("Login Successful", "You have logged in successfully!");

                        //fullscreen check
                        Stage loginStage = (Stage) passwordField.getScene().getWindow();
                        boolean wasFullScreen = loginStage.isFullScreen();

                        // Check if the account is already saved in the database
                        if (!isSaved) {
                            // Ask user to save their login info only if it's a new account
                            Optional<ButtonType> result = showSaveOptionDialog();
                            if (result.isPresent() && result.get() == ButtonType.YES) {
                                DatabaseHelper.saveAccount(account.getAddress(), account.getPassword());
                            }
                        }


                        // Proceed to the main window
                        if (!viewFactory.ismainViewInitialized()) {

                            viewFactory.showmainWindow(wasFullScreen);
                            viewFactory.updateStyles();
                        }

                        // Close login window
                        Stage stage = (Stage) errorLabel.getScene().getWindow();
                        viewFactory.closeStage(stage);
                        return;

                    case FAILED_BY_CREDENTIALS:
                        errorLabel.setText("Invalid credentials!!");
                        return;

                    case FAILED_BY_UNEXPECTED_ERROR:
                        errorLabel.setText("This Account is Already Logged In!!");
                        return;



                    default:
                        return;
                }
            });
        }
    }

    /**
     * Validates if the email and password fields are filled.
     *
     * @return true if valid, false otherwise
     */
    private boolean fieldsarevalid() {
        if (emailField.getText().isEmpty()) {
            errorLabel.setText("Email field is empty");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            errorLabel.setText("Password field is empty");
            return false;
        }
        return true;
    }

    /**
     * Shows a dialog asking if the user wants to save their login information.
     *
     * @return the user's choice as an Optional<ButtonType>.
     */
    private Optional<ButtonType> showSaveOptionDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Login Information");
        alert.setHeaderText("Do you want to save your login information?");
        alert.setContentText("This will allow you to quickly log in next time.");

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        return alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailSuggestions = new ContextMenu();

        // Populate default values for testing (optional)
//        emailField.setText("fortestingonly.0ki@gmail.com");
//        passwordField.setText("ubcm idmk oxkz gwmc");

        // Add listener to show suggestions on click
        emailField.setOnMouseClicked(this::showEmailSuggestions);

    }
    private void showEmailSuggestions(MouseEvent event) {
        // Clear old suggestions
        emailSuggestions.getItems().clear();

        // Fetch saved emails
        List<String> savedEmails = DatabaseHelper.getSavedEmails();

        for (String email : savedEmails) {
            MenuItem item = new MenuItem(email);

            // Set email and password when clicked
            item.setOnAction(e -> {
                emailField.setText(email);
                String password = DatabaseHelper.getPasswordForEmail(email);
                if (password != null) {
                    passwordField.setText(password);
                } else {
                    passwordField.clear();
                    errorLabel.setText("No saved password for this email.");
                }
            });

            emailSuggestions.getItems().add(item);
        }

        // Add a context menu to the email field for managing saved accounts
        ContextMenu manageContextMenu = new ContextMenu();
        MenuItem deleteOption = new MenuItem("Remove Selected Account");

        deleteOption.setOnAction(e -> {
            String selectedEmail = emailField.getText();
            if (selectedEmail == null || selectedEmail.isEmpty()) {
                errorLabel.setText("No email selected to remove.");
                return;
            }

            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Are you sure you want to remove this account?");
            alert.setContentText("Email: " + selectedEmail);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseHelper.deleteAccount(selectedEmail);
                refreshEmailSuggestions(); // Update suggestions after deletion
                errorLabel.setText("Account removed successfully.");
                emailField.clear();
                passwordField.clear();
            }
        });

        manageContextMenu.getItems().add(deleteOption);
        emailField.setContextMenu(manageContextMenu);

        // Show the email suggestions near the field
        if (!emailSuggestions.getItems().isEmpty()) {
            emailSuggestions.show(emailField, event.getScreenX(), event.getScreenY());
        }
    }
    private void refreshEmailSuggestions() {
        // Clear the existing suggestions
        emailSuggestions.getItems().clear();

        // Fetch updated saved emails
        List<String> updatedEmails = DatabaseHelper.getSavedEmails();

        // Add each updated email to the context menu
        for (String email : updatedEmails) {
            MenuItem item = new MenuItem(email);

            // Add the option to remove the account
            MenuItem removeItem = new MenuItem("Remove");

            // Context menu to include email selection and remove option
            ContextMenu itemContextMenu = new ContextMenu(item, removeItem);

            // Handle email selection (set the email and password)
            item.setOnAction(e -> {
                emailField.setText(email);
                String password = DatabaseHelper.getPasswordForEmail(email);
                if (password != null) {
                    passwordField.setText(password);
                } else {
                    passwordField.clear();
                    errorLabel.setText("No saved password for this email.");
                }
            });

            // Handle account removal
            removeItem.setOnAction(e -> {
                // Confirm deletion
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete");
                alert.setHeaderText("Are you sure you want to remove this account?");
                alert.setContentText("Email: " + email);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    DatabaseHelper.deleteAccount(email);
                    refreshEmailSuggestions(); // Refresh the suggestions after deletion
                    errorLabel.setText("Account removed successfully.");
                }
            });

            // Add the context menu to the menu item
            emailSuggestions.getItems().add(item);
        }
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
