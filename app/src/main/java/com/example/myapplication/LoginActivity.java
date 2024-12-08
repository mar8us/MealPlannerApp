package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity
{
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initWidgets();
    }

    private void initWidgets()
    {
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        Button buttonLogin = findViewById(R.id.loginButton);
        TextView buttonRegister = findViewById(R.id.registerTextView);

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser()
    {
        if (!isNetworkAvailable())
        {
            Toast.makeText(LoginActivity.this, "Sprawdź połączenie z internetem", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Wprowadź email i hasło", Toast.LENGTH_SHORT).show();
            return;
        }

        FireBaseAuth authFireBase = new FireBaseAuth();
        authFireBase.loginUser(email, password, new FireBaseAuth.FirebaseAuthLoginCallback()
        {
            @Override
            public void onSuccess(User user, String message)
            {
                SessionManager sessionManager = ((MyApplication) getApplication()).getSessionManager();
                sessionManager.userManager.getUserProfile(user.getUid(), new UserManager.FirebaseFirestoreGetUserCallback()
                {
                    @Override
                    public void onSuccess(User existingUser)
                    {
                        sessionManager.saveUserSession(existingUser);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage)
                    {
                        sessionManager.userManager.addUserProfile(user, new UserManager.FirebaseFirestoreCallback()
                        {
                            @Override
                            public void onSuccess(String message)
                            {
                                sessionManager.saveUserSession(user);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMessage)
                            {
                                Toast.makeText(LoginActivity.this, "Błąd dodawania profilu: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage)
            {
                Toast.makeText(LoginActivity.this, "Błąd logowania: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}