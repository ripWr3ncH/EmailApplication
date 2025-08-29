package com.example.emailapp;

import javax.mail.Store;
import java.util.Properties;
import javax.mail.Session;

public class Account {
    private String address;
    private String password;
    private Properties properties;
    private Store store;
    private Session session;
    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }



    public String getAddress() {
        return address;
    }



    public Store getStore() {
        return store;
    }

    @Override
    public String toString() {
        return address;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getPassword() {
        return password;
    }
    //disconnect:
    public void disconnect() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   // public Account() {}
    public Account(String address, String password) {
        this.address = address;
        this.password = password;
        properties = new Properties();
        properties.put("incomingHost", "imap.gmail.com");
        properties.put("mail.store.protocol", "imaps");

        properties.put("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.host", "smtp.gmail.com");
        properties.put("mail.smtps.auth", "true");
        properties.put("outgoingHost", "smtp.gmail.com");
    }
}
