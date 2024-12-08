package com.example.myapplication;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class User
{
    private String uid;
    private String email;
    private String displayName;
    private String profilePictureUrl;

    public User()
    {
    }

    public User(FirebaseUser firebaseUser)
    {
        if (firebaseUser == null)
            return;
        this.uid = firebaseUser.getUid();
        this.email = firebaseUser.getEmail();
        this.displayName = firebaseUser.getDisplayName();
        if (firebaseUser.getPhotoUrl() != null)
            this.profilePictureUrl = firebaseUser.getPhotoUrl().toString();
        else
            this.profilePictureUrl = null;
    }

    public String getUid()
    {
        return uid;
    }

    public String getEmail()
    {
        return email;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getProfilePictureUrl()
    {
        return profilePictureUrl;
    }

    public Map<String, Object> toMap()
    {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("uid", this.uid);
        userMap.put("email", this.email);
        userMap.put("displayName", this.displayName);
        userMap.put("profilePictureUrl", this.profilePictureUrl);
        return userMap;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                '}';
    }
}