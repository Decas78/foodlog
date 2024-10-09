package com.example.foodlog.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageManager {

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    // Uploads a photo and returns the URL to access the uploaded photo
    suspend fun uploadPhoto(photoUri: Uri): String {
        val photoRef = storageRef.child("meal_photos/${photoUri.lastPathSegment}")
        photoRef.putFile(photoUri).await()  // Wait for the upload to complete
        return photoRef.downloadUrl.await().toString()  // Return the download URL
    }
}