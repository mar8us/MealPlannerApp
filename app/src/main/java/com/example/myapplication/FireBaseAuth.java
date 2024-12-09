package com.example.myapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FireBaseAuth
{
    private final FirebaseAuth firebaseAuth;

    public FireBaseAuth()
    {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginUser(String email, String password, FirebaseAuthLoginCallback callback)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                    {
                        User user = new User(getCurrentUser());
                        callback.onSuccess(user, "Pomyślnie zalogowano!");
                    }
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void logoutUser()
    {
        firebaseAuth.signOut();
    }

    public void registerUser(String email, String password, String displayName, FirebaseAuthRegisterCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user == null)
                            return;

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(updateTask ->
                                {
                                    if (updateTask.isSuccessful())
                                        callback.onSuccess("Pomyślnie zalogowano");
                                    else
                                        callback.onFailure(updateTask.getException().getMessage());
                                });

                    }
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public FirebaseUser getCurrentUser()
    {
        return firebaseAuth.getCurrentUser();
    }

    public void resetPassword(String email, FirebaseAuthRegisterCallback callback)
    {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                        callback.onSuccess("Password reset email sent.");
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void changePassword(String newPassword, FirebaseAuthLoginCallback callback)
    {
        FirebaseUser user = getCurrentUser();
        if (user == null)
        {
            callback.onFailure("Użytkownik nie jest zalogowany.");
            return;
        }
        user.updatePassword(newPassword)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                        callback.onSuccess(new User(user),"Hasło zostało zmienione.");
                    else
                        callback.onFailure(task.getException().getMessage());
                });

    }

    public void deleteUser(FirebaseAuthRegisterCallback callback)
    {
        FirebaseUser user = getCurrentUser();
        if (user == null)
        {
            callback.onFailure("Użytkownik nie jest zalogowany.");
            return;
        }
        user.delete().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
                callback.onSuccess("Konto zostało usunięte.");
            else
                callback.onFailure(task.getException().getMessage());
        });
    }

    public interface FirebaseAuthLoginCallback
    {
        void onSuccess(User user, String message);
        void onFailure(String errorMessage);
    }

    public interface FirebaseAuthRegisterCallback
    {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }
}
