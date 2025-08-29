package com.example.emailapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.net.URL;
import java.util.ResourceBundle;

public class ComposeMessageController extends AbsController implements Initializable {

    private List<File> attachments = new ArrayList<File>();

    @FXML
    private Label errorLabel;

    @FXML
    private ChoiceBox<Account>emailAccountChoice;

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;
    @FXML
    void attachBtnAction() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null){
            attachments.add(selectedFile);
        }
    }

    @FXML
    void sendButtonAction() {
        EmailSenderService emailSenderService = new EmailSenderService(
                emailAccountChoice.getValue(),
                subjectTextField.getText(),
                recipientTextField.getText(),
                htmlEditor.getHtmlText(),
                attachments
        );
        emailSenderService.start();
        emailSenderService.setOnSucceeded(e->{
            EmailSendingResult emailSendingResult = emailSenderService.getValue();
            switch (emailSendingResult) {
                case SUCCESS:
                    Stage stage = (Stage) recipientTextField.getScene().getWindow();
                    viewFactory.closeStage(stage);
                    break;
                case FAILED_BY_PROVIDER:
                    errorLabel.setText("Provider error!");
                    break;
                case FAILED_BY_UNEXPECTED_ERROR:
                    errorLabel.setText("Unexpected error!");
                    break;
            }
        });
    }
    public ComposeMessageController() {}
    public ComposeMessageController(EmailManager emailManager,ViewFactory viewFactory,String fxmlName) {
        super(emailManager,viewFactory,fxmlName);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailAccountChoice.setItems(emailManager.getEmailAccounts());
        emailAccountChoice.setValue(emailManager.getEmailAccounts().get(0));
    }
}
