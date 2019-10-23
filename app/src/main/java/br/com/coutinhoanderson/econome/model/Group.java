package br.com.coutinhoanderson.econome.model;

import java.util.List;

public class Group {
    private List<User> users;
    private String name;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
