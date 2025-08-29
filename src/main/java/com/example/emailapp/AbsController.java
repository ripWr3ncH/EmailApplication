package com.example.emailapp;

public abstract class AbsController  {

    protected EmailManager emailManager;
    protected ViewFactory viewFactory;
    private String fxmlName;

    public AbsController(){}
    public AbsController( EmailManager emailManager, ViewFactory viewFactory,String fxmlName) {
        this.fxmlName = fxmlName;
        this.viewFactory = viewFactory;
        this.emailManager = emailManager;
    }
    public String getFxmlName()
    {
        return fxmlName;
    }
}
