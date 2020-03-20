package com.darvesh.hi.user;

public class UserObject {

    private String uid, name, phone;
    private boolean selected=false;

    public UserObject(String name, String phone, String uid){
        this.name=name;
        this.phone=phone;
        this.uid=uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid(){
        return uid;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
