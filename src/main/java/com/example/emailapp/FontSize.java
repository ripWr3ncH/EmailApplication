package com.example.emailapp;

public enum FontSize {
    SMALL,
    MEDIUM,
    LARGE;

    public static String getCssPath(FontSize size) {
        switch (size) {
            case SMALL:
                return "css/fontSmall.css";
            case MEDIUM:
                return "css/fontMedium.css";
            case LARGE:
                return "css/fontBig.css";
            default:
                return null;
        }
    }
}
