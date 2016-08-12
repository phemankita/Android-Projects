package com.example.hemankita.myrxproject;

/**
 * Created by Hemankita on 6/18/2016.
 */
public class Contact {
    private String username;
    private String image;
    private String publickey;
    private String status;
    public Contact(){

    }

    public Contact(String username, String image, String publickey, String status){
       this.username=username;
       this.image=image;
       this.publickey=publickey;
        this.status=status;
    }

    public String getContact()
    {
        return username;
    }

    public String getImage()
    {
        return image;
    }

    public String getPublickey()
    {
        return publickey;
    }

    public String getStatus() {return status;}

    public void setContact(String username)
    {
        this.username=username;
    }

    public void setImage(String image)
    {
        this.image=image;
    }
    public void setPublickey(String publickey)
    {
        this.publickey=publickey;
    }
    public void setStatus(String status)
    {
        this.status=status;
    }

}
