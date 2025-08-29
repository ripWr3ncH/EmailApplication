package com.example.emailapp;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;

public class FolderUpdaterService extends Service {

    private List<Folder> folderList;

    public FolderUpdaterService(List<Folder> folderList) {
        this.folderList = folderList;
    }
    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                for(;;){
                    try {
                        Thread.sleep(5000);
                        for(Folder folder : folderList)
                        {
                            if(folder.getType() != Folder.HOLDS_FOLDERS &&  folder.isOpen()){
                                folder.getMessageCount();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }
}