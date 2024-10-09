package com.example.foodlog

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PhotoUploadManager {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    // Function to upload the photo and return the download URL via a callback
    fun uploadPhoto(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit = {}) {
        val fileName = "images/${System.currentTimeMillis()}.jpg"
        val photoRef = storageReference.child(fileName)

        photoRef.putFile(imageUri)
            .addOnSuccessListener {
                // Get the download URL
                photoRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // Pass the URL to the onSuccess callback
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Pass the exception to the onFailure callback
            }
    }
}