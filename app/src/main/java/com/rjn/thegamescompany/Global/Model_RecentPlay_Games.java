package com.rjn.thegamescompany.Global;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Model_RecentPlay_Games extends RealmObject {

    /*@PrimaryKey
    @Index
    private long indexId;*/

    String id = "";
    String name = "";
    String Html_flash = "";

    public String getHtml_flash() {
        return Html_flash;
    }

    public void setHtml_flash(String html_flash) {
        Html_flash = html_flash;
    }

    public String getPname() {

        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    String Pname = "";

    public String getCname() {
        return Cname;
    }

    public void setCname(String cname) {
        Cname = cname;
    }

    String Cname = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    String cate_id = "";
    String icon = "";

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String gameID = "";
    public String appName = "";

}
