package com.idohayun.bracelethackathon;

public class Tender {
    String name;
    String password;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    enum Permissions {
        Magen_David_Adom, Doctor, volunteer;
    }

    Permissions permission;

    public Tender(int id, String name, String password, Permissions permission) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.permission = permission;
    }

    public Permissions getPermission() {
        return permission;
    }

    public void setPermission(Permissions permission) {
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
