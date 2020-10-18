package com.rjn.thegamescompany.Global;

public class Enum_Games {

    public enum NavigationType {
        DASHBOARD, CATEGORYALL, CATEGORYFILTER, PUBLISHERALL, PUBLISHERFILTER, CATEGORYGAMES, PUBLISHERGAMES, TRENDINGGAMES, NEWADDEDGAMES, ALLGAMES, FAVORITEGAMES, SEARCHGAMEFILTER,SINGLEGAME;
    }

    public static NavigationType getStringToNavigationType(String strTitle) {
        try {
            return NavigationType.valueOf(strTitle);
        } catch (Exception e) {
            e.printStackTrace();
            return NavigationType.DASHBOARD;
        }
    }

}
