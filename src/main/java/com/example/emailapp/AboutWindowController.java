package com.example.emailapp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutWindowController extends AbsController implements Initializable {

    public AboutWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    private Label appNameLabel;

    @FXML
    private Label sloganLabel;

    @FXML
    private Label appInfoLabel;

    @FXML
    private Label developerInfoLabel;

    @FXML
    void closeButtonAction() {
        Stage stage = (Stage) appNameLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appNameLabel.setText("Inboxio");
        sloganLabel.setText("Read, Respond, Relax - Inboxio");
        appInfoLabel.setText("Inboxio is an intuitive and user-friendly email client application "
                + "designed to enhance your email experience. "
                + "Easily manage your emails, organize your inbox, and streamline communication.");
        developerInfoLabel.setText("Developed by:\nDewan Salman Rahman Zisan\nEmail: dewanzisan1@gmail.com");
    }
}
