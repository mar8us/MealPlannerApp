package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class SessionManager
{
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER = "user";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    public UserManager userManager;

    public SessionManager(Context context)
    {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
        userManager = new UserManager();
    }

    public void saveUserSession(User user)
    {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    public User getUserSession()
    {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public void clearSession()
    {
        editor.remove(KEY_USER);
        editor.apply();
    }

    public boolean isUserLoggedIn()
    {
        return getUserSession() != null;
    }
}