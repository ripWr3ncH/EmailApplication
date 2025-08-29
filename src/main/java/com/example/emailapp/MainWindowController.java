package com.example.emailapp;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebView;

import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainWindowController extends AbsController implements Initializable {

    private MenuItem markUnreadMenuItem = new MenuItem("mark as unread");
    private MenuItem deleteMessageMenuItem = new MenuItem("delete message");
    private MenuItem showMessageDetailsMenuItem = new MenuItem("view details");
    @FXML
    private TableView<EmailMessage> emailTableView;
    @FXML
    private TableColumn<EmailMessage, String> recipientCol;

    @FXML
    private TableColumn<EmailMessage, String> senderCol;

    @FXML
    private TableColumn<EmailMessage, String> subjectCol;

    @FXML
    private TableColumn<EmailMessage, SizeInteger> sizeCol;

    @FXML
    private TableColumn<EmailMessage, Date>dateCol;

    @FXML
    private TreeView<String> emailTreeView;

   @FXML
   private WebView emailWebView;

    private MessageRendererService messageRendererService;

    public MainWindowController() {}
    public MainWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
      viewFactory.showoptionsWindow();
      viewFactory.updateStyles();
    }
    @FXML
    void addAccountAction() {
      viewFactory.showloginWindow();
        viewFactory.updateStyles();

    }
    @FXML
    void aboutAction() {
        viewFactory.showAboutWindow();
        viewFactory.updateStyles();
    }

    @FXML
    void composeMessageAction() {
        viewFactory.showComposeMessageWindow();
        viewFactory.updateStyles();
    }
//    @FXML
//    void closeButton() {
//        Stage stage = (Stage) emailTableView.getScene().getWindow();
//    viewFactory.closeStage(stage);
//    }
@FXML
void closeButton() {
    boolean proceed = showCloseAppConfirmation();
    if (proceed) {
        // Close the application
        Platform.exit(); // Gracefully shuts down the JavaFX application
        System.exit(0);  // Ensures JVM exits
    }
}



    private boolean showCloseAppConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Application");
        alert.setHeaderText("Are you sure you want to close the application?");
        alert.setContentText("Any unsaved progress will be lost.");

        // Wait for user response
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpEmailsTreeView();
        setUpEmailsTableView();
        setUpFolderSelection();
        setUpBoldRows();
        setUpMessageRendererService();
        setUpMessageSelection();
        setUpContextMenus();

    }
    private void setUpContextMenus() {
        markUnreadMenuItem.setOnAction(event -> {
            emailManager.setUnRead();
        });
        deleteMessageMenuItem.setOnAction(event -> {
            emailManager.deleteSelectedMessage();
            emailWebView.getEngine().loadContent("");
        });
        showMessageDetailsMenuItem.setOnAction(event -> {
            viewFactory.showEmailDetailsWindow();
            viewFactory.updateStyles();
        });
    }

    private void setUpMessageSelection() {
        emailTableView.setOnMouseClicked(event -> {
            EmailMessage emailMessage = emailTableView.getSelectionModel().getSelectedItem();
            if(emailMessage != null){
                emailManager.setSelectedMessage(emailMessage);
                if(!emailMessage.isRead()){
                    emailManager.setRead();
                }
                messageRendererService.setEmailMessage(emailMessage);
                messageRendererService.restart();
            }
        });
    }

    private void setUpMessageRendererService() {
        messageRendererService = new MessageRendererService(emailWebView.getEngine());
    }

    private void setUpFolderSelection() {
        emailTreeView.setOnMouseClicked(event -> {
            EmailTreeItem<String> item = (EmailTreeItem<String>) emailTreeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                emailManager.setSelectedFolder(item);
                emailTableView.setItems(item.getEmailMessages());
            }
        });
    }

    private void setUpEmailsTableView() {
        senderCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, String>("sender")));
        subjectCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, String>("subject")));
        recipientCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, String>("recipient")));
        sizeCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, SizeInteger>("size")));
        dateCol.setCellValueFactory((new PropertyValueFactory<EmailMessage, Date>("date")));


        emailTableView.setContextMenu(new ContextMenu(markUnreadMenuItem, deleteMessageMenuItem, showMessageDetailsMenuItem));
    }

    private void setUpEmailsTreeView() {
        emailTreeView.setRoot(emailManager.getFoldersRoot());
        emailTreeView.setShowRoot(false);
    }
    private void setUpBoldRows() {
        emailTableView.setRowFactory(new Callback<TableView<EmailMessage>, TableRow<EmailMessage>>() {
            @Override
            public TableRow<EmailMessage> call(TableView<EmailMessage> param) {
                return new TableRow<EmailMessage>(){
                    @Override
                    protected void updateItem(EmailMessage item, boolean empty){
                        super.updateItem(item, empty);
                        if(item != null) {
                            if(item.isRead()){
                                setStyle("");
                            } else {
                                setStyle("-fx-font-weight: bold");
                            }
                        }
                    }
                };
            }
        });
    }

    //logout option handle
    @FXML
    void logoutAction() {
        if (!emailManager.getEmailAccounts().isEmpty()) {
            if (emailManager.getEmailAccounts().size() == 1) {
                // Show warning dialog
                boolean proceed = showSingleAccountLogoutWarning();
                if (proceed) {
                    // If user confirms, logout and close application
                    Account accountToLogout = emailManager.getEmailAccounts().get(0);
                    logoutAndCloseApplication(accountToLogout);
                }
            } else {
                // If multiple accounts, let the user select which account to log out
                Account accountToLogout = showAccountSelectionDialog();
                if (accountToLogout != null) {
                    emailManager.logoutEmailAccount(accountToLogout);
                    refreshAccountUI();
                }
            }
        }
    }

    private boolean showSingleAccountLogoutWarning() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Warning");
        alert.setHeaderText("You are logging out of the last account");
        alert.setContentText("Logging out will close the application. Do you want to proceed?");

        // Wait for user response
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void logoutAndCloseApplication(Account accountToLogout) {
        emailManager.logoutEmailAccount(accountToLogout); // Perform the logout

        // Stop any background services explicitly
        emailManager.stopBackgroundServices();

        // Ensure application exits promptly
        Stage stage = (Stage) emailTableView.getScene().getWindow();
        Platform.exit(); // Gracefully exit the JavaFX thread
        System.exit(0);  // Ensure JVM exits
    }

    private Account showAccountSelectionDialog() {
        ChoiceDialog<Account> dialog = new ChoiceDialog<>(null, emailManager.getEmailAccounts());
        dialog.setTitle("Logout");
        dialog.setHeaderText("Select an account to logout");
        dialog.setContentText("Accounts:");
        return dialog.showAndWait().orElse(null);
    }

    private void refreshAccountUI() {
        emailTreeView.setRoot(emailManager.getFoldersRoot());
        emailTableView.setItems(null);
        emailWebView.getEngine().loadContent("");
    }


}