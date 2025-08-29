package com.example.emailapp;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.xml.transform.dom.DOMLocator;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionWindowController extends AbsController implements Initializable {
    public OptionWindowController(){}

    public OptionWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    private Slider fontSizePick;

    @FXML
    private ChoiceBox<ColorTheme> themeSelector;


    @FXML
    void applyButtonAction() {
       viewFactory.setColorTheme(themeSelector.getValue());
       viewFactory.setFontSize(FontSize.values()[(int)(fontSizePick.getValue())]);
       viewFactory.updateStyles();

    }

    @FXML
    void cancelButtonAction() {
       Stage stage = (Stage) fontSizePick.getScene().getWindow();
       viewFactory.closeStage(stage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
         setUpThemePicker();
         setUpSizePicker();
    }

    private void setUpSizePicker() {
        fontSizePick.setMin(0);
        fontSizePick.setMax(FontSize.values().length - 1);
        fontSizePick.setValue(viewFactory.getFontSize().ordinal());
        fontSizePick.setMajorTickUnit(1);
        fontSizePick.setMinorTickCount(0);
        fontSizePick.setBlockIncrement(1);
        fontSizePick.setSnapToTicks(true);
        fontSizePick.setShowTickMarks(true);
        fontSizePick.setShowTickLabels(true);
        fontSizePick.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                int i = object.intValue();

                return FontSize.values()[i].toString();
            }

            @Override
            public Double fromString(String s) {
                return null;
            }
        });
        fontSizePick.valueProperty().addListener((obs,oldVal,newVal)->{
            fontSizePick.setValue(newVal.intValue());
        });

    }

    private void setUpThemePicker() {
        themeSelector.setItems(FXCollections.observableArrayList(ColorTheme.values()));
        themeSelector.setValue(viewFactory.getColorTheme());
    }

}




