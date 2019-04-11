package com.idohayun.bracelethackathon;

public class Tender {
    int id;
    String name;
    String password;
    enum Permissions
    {
        Doctor,Paramedic;
    }
    Permissions permission;

    public Permissions getPermission() {
        return permission;
    }

    public void setPermission(Permissions permission) {
        this.permission = permission;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
