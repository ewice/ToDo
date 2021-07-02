package com.example.todo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Todo implements Serializable {

    @SerializedName("id")
    private long id;

    @SerializedName("done")
    private boolean done;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("expiry")
    private long expiry;

    @SerializedName("favourite")
    private boolean favourite;

    @SerializedName("contacts")
    private List<String> contacts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public void addContact(String id) { contacts.add(id); }

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
}
