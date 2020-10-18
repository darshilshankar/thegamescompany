package com.rjn.thegamescompany.Global;

import java.io.Serializable;

public class Element implements Serializable {

    String id = "";
    String name = "";
    String cate_id = "";
    String icon = "";

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image = "";
    String email = "";
    String pub_id = "";

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    String app_id = "";
    String html_flash = "";

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPub_id() {
        return pub_id;
    }

    public void setPub_id(String pub_id) {
        this.pub_id = pub_id;
    }

    public String getHtml_flash() {
        return html_flash;
    }

    public void setHtml_flash(String html_flash) {
        this.html_flash = html_flash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getPosition_order() {
        return position_order;
    }

    public void setPosition_order(String position_order) {
        this.position_order = position_order;
    }

    String url = "";
    String counter = "";
    String position_order = "";
    String pname = "";
    String cname = "";

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String Title = "";

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String Description = "";

    public String getBGColor() {
        return BGColor;
    }

    public void setBGColor(String BGColor) {
        this.BGColor = BGColor;
    }

    public String BGColor = "";
}
