package com.example.rommies;

public class User
{
    private String email,name, Uid, apartment_Key = null;
    public User(){}
    public User(String e, String n)
    {
        this.email = e;
        this.name = n;
    }
    public String getEmail(){return email;}
    public String getName(){return name;}
    public String getUid(){return Uid;}
    public void setUid(String uid){this.Uid = uid;}

    public String getAprKey()
    {
        return apartment_Key;
    }
}
