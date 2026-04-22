package com.reservationstudio.model;

public class User {

    public enum Role{
        SERVER, MANAGER
    }

    private String username;
    private String password;
    private Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    //Getters

    public String getUsername() {return username; }
    public String getPassword() {return password; }
    public Role getRole()       {return role; }

    //Parses role from CSV file
    public static Role parseRole(String s) {
        if (s.trim().equalsIgnoreCase("manager")) {
            return Role.MANAGER;
        } else {
            return Role.SERVER;
        }
    }
    @Override
    public String toString(){
        return username + " | " + role;
    }
}
