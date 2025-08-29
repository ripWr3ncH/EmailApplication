module com.example.emailapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires activation;
    requires java.mail;
    requires java.sql;
    requires java.desktop;
    requires spring.security.crypto;


    opens com.example.emailapp to javafx.fxml;

    exports com.example.emailapp;
}