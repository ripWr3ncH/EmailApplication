package com.example.emailapp;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class emailApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {


        StackPane splashRoot = new StackPane();
        splashRoot.setStyle("-fx-background-color: linear-gradient(to bottom,  #9bbec4, #b2ccd1, #c5d6d9);");


        Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icons/ttttttttaww-01.png")));
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(300);
        logoView.setPreserveRatio(true);
        splashRoot.getChildren().add(logoView);

        //Splash Scene
        Scene splashScene = new Scene(splashRoot, 475.0, 491.0); // Adjust size to your liking
        Stage splashStage = new Stage();
        Image spappIcon = new Image(getClass().getResourceAsStream("icons/inboxioIcon.png"));
        splashStage.getIcons().add(spappIcon);
        splashStage.initStyle(StageStyle.UNDECORATED); // No window decorations
        splashStage.setScene(splashScene);

        // Show Splash
        splashStage.show();


        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), splashRoot);
        fadeIn.setFromValue(0); // Start fully transparent
        fadeIn.setToValue(1);   // End fully visible
        fadeIn.setOnFinished(e -> {
            // Close Splash and Open Main App
            splashStage.close();

            try {
                // initialize main application
                ViewFactory viewFactory = new ViewFactory(new EmailManager());
                viewFactory.showloginWindow();
                viewFactory.updateStyles();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        fadeIn.play();
    }

    public static void main(String[] args) {
        launch();
    }
}
