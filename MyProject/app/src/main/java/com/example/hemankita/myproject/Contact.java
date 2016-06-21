package com.example.hemankita.myproject;

/**
 * Created by Hemankita on 6/18/2016.
 */
public class Contact {
    private String name;

    public Contact(String name){
       this.setContact(name);
    }

    public String getContact() {return name;}

    public void setContact(String name)
    {
        this.name=name;
    }

    public String toString()
    {
        return name;
    }
}
