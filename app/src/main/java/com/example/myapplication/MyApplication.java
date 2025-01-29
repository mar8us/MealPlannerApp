package com.example.myapplication;

import android.app.Application;

import Firebase.SessionManager;

public class MyApplication extends Application
{
    private SessionManager sessionManager;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sessionManager = new SessionManager(this);
    }

    public SessionManager getSessionManager()
    {
        return sessionManager;
    }
}