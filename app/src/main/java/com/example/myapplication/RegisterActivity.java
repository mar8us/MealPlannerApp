package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity
{
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText confirmPasswordEditText;
    private EditText nickTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        initWidgets();
    }

    private void initWidgets()
    {
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        nickTextEdit =  findViewById(R.id.nickEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginViewButton = findViewById(R.id.loginTextView);

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerUser();
            }
        });

        loginViewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginViewExecute();
            }
        });
    }

    void registerUser()
    {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confrimPassword = confirmPasswordEditText.getText().toString().trim();
        String nickEditText = nickTextEdit.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Wprowadź email i hasło", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confrimPassword))
        {
            Toast.makeText(this, "Hasła nie są zgodne", Toast.LENGTH_SHORT).show();
            return;
        }

        FireBaseAuth auth = new FireBaseAuth();
        auth.registerUser(email, password, nickEditText, new FireBaseAuth.FirebaseAuthRegisterCallback()
        {
            @Override
            public void onSuccess(String message)
            {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                loginViewExecute();
            }

            @Override
            public void onFailure(String errorMessage)
            {
                Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginViewExecute()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}