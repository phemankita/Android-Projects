package com.example.hemankita.myrxproject.stages;

import com.example.hemankita.myrxproject.Crypto;

import java.security.PublicKey;

/**
 * Created by Hemankita on 8/7/2016.
 */
public class UserInfo {
    public final String username;
    public final String image;
    public final PublicKey publicKey;
    public UserInfo(String username, String image, String keyString){
        this.username = username;
        this.image = image;
        this.publicKey = Crypto.getPublicKeyFromString(keyString);
    }
}
