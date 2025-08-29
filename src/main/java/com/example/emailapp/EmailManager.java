package com.example.emailapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import javax.mail.Folder;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Flags;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class EmailManager {

    private EmailMessage selectedMessage;
    private EmailTreeItem<String> selectedFolder;
    private ObservableList<Account>emailAccounts = FXCollections.observableArrayList();
    private IconResolver iconResolver = new IconResolver();
    public ObservableList<Account> getEmailAccounts() {
        return emailAccounts;
    }

    public EmailMessage getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(EmailMessage selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public EmailTreeItem<String> getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(EmailTreeItem<String> selectedFolder) {
        this.selectedFolder = selectedFolder;
    }
    //folder handlings
    private EmailTreeItem<String> foldersRoot = new EmailTreeItem<String>("");
    private FolderUpdaterService folderUpdaterService;
    private List<Folder> folderList = new ArrayList<Folder>();

    public EmailManager() {
      folderUpdaterService = new FolderUpdaterService(folderList);
      folderUpdaterService.start();
    }


    public List<Folder> getFolderList() {
      return this.folderList;
    }

    public TreeItem<String> getFoldersRoot() {
        return foldersRoot;
    }

    public void addEmailAccount(Account emailAccount) {
        // Check if the account is already logged in
        for (Account account : emailAccounts) {
            if (account.getAddress().equals(emailAccount.getAddress())) {
                // Show alert window
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Account Already Logged In");
                alert.setHeaderText(null);
                alert.setContentText("The email account \"" + emailAccount.getAddress() + "\" is already logged in.");
                alert.showAndWait(); // Wait for user to dismiss the alert
                return; // Exit the method
            }
        }

        // Proceed to add the account if not already logged in
        emailAccounts.add(emailAccount);
        EmailTreeItem<String> treeItem = new EmailTreeItem<>(emailAccount.getAddress());
        treeItem.setGraphic(iconResolver.getIconForFolder(emailAccount.getAddress()));

        FetchFolderService fetchFolderService = new FetchFolderService(emailAccount.getStore(), treeItem, folderList);
        fetchFolderService.start();
        foldersRoot.getChildren().add(treeItem);
    }
    public void setRead() {
        try {
            selectedMessage.setRead(true);
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, true);
            selectedFolder.decrementMessagesCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setUnRead() {
        try {
            selectedMessage.setRead(false);
            selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, false);
            selectedFolder.incrementMessagesCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedMessage() {
        try {
            selectedMessage.getMessage().setFlag(Flags.Flag.DELETED, true);
            selectedFolder.getEmailMessages().remove(selectedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void logoutEmailAccount(Account account) {
        if (emailAccounts.contains(account)) {
            // Remove the account
            emailAccounts.remove(account);

            // Remove associated folders from the folder tree
            foldersRoot.getChildren().removeIf(treeItem -> treeItem.getValue().equals(account.getAddress()));

            // Disconnect the account (cleanup resources, close connections, etc.)
            account.disconnect();

            // Clear any selected account-specific data if this was the active account
            if (selectedFolder != null && selectedFolder.getValue().equals(account.getAddress())) {
                selectedFolder = null;
                selectedMessage = null;
            }
        }
    }
    public void stopBackgroundServices() {
        if (folderUpdaterService != null && folderUpdaterService.isRunning()) {
            folderUpdaterService.cancel(); // Stop the folder updater
        }


        folderList.clear(); // Clear folder references
    }
}
