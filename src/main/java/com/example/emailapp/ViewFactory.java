package com.example.emailapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ViewFactory {
    private EmailManager emailManager;
    private ArrayList<Stage>activeStages;
    private boolean mainViewInitialized = false;
    private Stage mainWindowStage; // Keep track of the main window's stage




    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
        activeStages = new ArrayList<>();
    }

    public boolean ismainViewInitialized() {
        return mainViewInitialized;
    }

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    //view Options
    private ColorTheme colorTheme = ColorTheme.LIGHT;
    private FontSize fontSize = FontSize.MEDIUM;


    private void initializeStage(AbsController controller,boolean isFullScreen) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(controller.getFxmlName()));
        fxmlLoader.setController(controller);

        Parent parent = null;
        try{
            parent = fxmlLoader.load();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
        Scene scence = new Scene(parent);
        Stage stage = new Stage();
        Image appIcon = new Image(getClass().getResourceAsStream("icons/inboxioIcon.png"));
        stage.getIcons().add(appIcon);
        stage.setScene(scence);
        stage.setFullScreen(isFullScreen);

        stage.show();
        activeStages.add(stage);
        if (controller instanceof MainWindowController) {
            mainWindowStage = stage;
        }
    }


    public void showloginWindow() {
        System.out.println("loginwindow");
        AbsController controller = new LoginPageController(emailManager,this,"loginPage.fxml");
        initializeStage(controller,false);

    }

    public void showmainWindow(boolean isFullScreen ) {
        System.out.println("Mainwindow");
        AbsController controller = new MainWindowController(emailManager,this,"mainWindow.fxml");
        initializeStage(controller,isFullScreen);
        mainViewInitialized = true;
    }

    public void showoptionsWindow() {
        System.out.println("Optionswindow");
        AbsController controller = new OptionWindowController(emailManager,this,"optionsWindow.fxml");
        initializeStage(controller,false);
    }
    public void showEmailDetailsWindow(){
        System.out.println("EmailDetailswindow");
        AbsController controller = new EmailDetailsController(emailManager, this, "EmailDetailsWindow.fxml");
        initializeStage(controller,false);
    }
    public void showComposeMessageWindow() {
        System.out.println("ComposeMessage_window");
        AbsController controller = new ComposeMessageController(emailManager,this,"ComposeMessageWindow.fxml");
        initializeStage(controller,false);
    }
    public void showAboutWindow() {
        AbsController controller = new AboutWindowController(emailManager, this, "aboutWindow.fxml");
        initializeStage(controller,false);
    }

    public void closeStage(Stage stage) {
        stage.close();
        activeStages.remove(stage);
    }


    public void updateStyles() {
      for(Stage stage : activeStages) {
          Scene scene = stage.getScene();


          //apply css
          scene.getStylesheets().clear();
          scene.getStylesheets().add(getClass().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
          scene.getStylesheets().add(getClass().getResource(FontSize.getCssPath(fontSize)).toExternalForm());

      }
    }
    public Stage getMainWindowStage() {
        return mainWindowStage;
    }



}
