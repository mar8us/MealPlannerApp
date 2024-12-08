package com.example.myapplication;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class UserManager
{
    private FirebaseFirestore firestore;

    public UserManager()
    {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addUserProfile(User user, FirebaseFirestoreCallback callback) {
        DocumentReference userDoc = firestore.collection("users").document(user.getUid());
        userDoc.set(user.toMap())
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                    {
                        callback.onSuccess("Profil został dodany");
                    }
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void getUserProfile(String userId, FirebaseFirestoreGetUserCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                            callback.onSuccess(document.toObject(User.class));
                        else
                            callback.onFailure("Nie znaleziono profilu");
                    }
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void updateUserProfile(User user, FirebaseFirestoreCallback callback)
    {
        DocumentReference userDoc = firestore.collection("users").document(user.getUid());
        userDoc.update(user.toMap())
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                        callback.onSuccess("Profil został zaktualizowany.");
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public void deleteUserProfile(String userId, FirebaseFirestoreCallback callback)
    {
        DocumentReference userDoc = firestore.collection("users").document(userId);
        userDoc.delete()
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                        callback.onSuccess("Profil został usunięty.");
                    else
                        callback.onFailure(task.getException().getMessage());
                });
    }

    public interface FirebaseFirestoreCallback
    {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public interface FirebaseFirestoreGetUserCallback
    {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
}