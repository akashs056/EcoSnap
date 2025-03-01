package com.example.ecosnap.auth.repositories

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class AuthRepo(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val sharedPref = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun signInWithEmailPassword(email: String, password: String, isWorker: Boolean): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val task = auth.signInWithEmailAndPassword(email, password).await()
                if (task.user != null) {
                    saveUserDetails("Demo", email,isWorker)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Authentication Failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun signUpWithEmailPassword(name: String, email: String, password: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val task = auth.createUserWithEmailAndPassword(email, password).await()
                if (task.user != null) {
                    saveUserDetails(name, email,false)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Authentication Failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun saveUserDetails(name: String, email: String, isWorker: Boolean) {
        editor.putString("name", name)
        editor.putString("email", email)
        editor.putBoolean("isWorker", isWorker)
        editor.apply()
    }

    fun clearAllSharedPreferences() {
        editor.clear()  // Clear all data
        editor.apply()   // Apply changes asynchronously
        Log.d("EcoSnapDebug", "in auth repo cleared all shared pref")
    }

    fun logout() {
        auth.signOut()
        clearAllSharedPreferences()
    }

    suspend fun saveUserToBackend(name: String, email: String, password: String): Result<Boolean> {
        val url = "https://eco-snap-server.vercel.app/user/create"
        Log.d("EcoSnapDebug", "in auth repo saveUserToBackend url: $url")
        val requestBody = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )
        Log.d("EcoSnapDebug", "in auth repo saveUserToBackend requestBody: $requestBody")
        val client = OkHttpClient()
        return withContext(Dispatchers.IO){
            try {
                val jsonBody = JSONObject(requestBody).toString()
                val body = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("EcoSnapDebug", "in auth repo saveUserToBackend response is successful}")
                    Result.success(true)
                } else {
                    Log.d("EcoSnapDebug", "in auth repo saveUserToBackend response is not successful}")
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            } catch (e: IOException) {
                Log.d("EcoSnapDebug", "in auth repo saveUserToBackend exception ${e.message}")
                Result.failure(e)
            }
        }
    }

    fun getIsWorker() : Boolean {
        return sharedPref.getBoolean("isWorker", false)
    }

}