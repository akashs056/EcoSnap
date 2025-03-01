package com.example.ecosnap.repository

import android.content.Context
import android.util.Log
import com.example.ecosnap.models.Event
import com.example.ecosnap.models.LeaderboardCard
import com.example.ecosnap.models.User
import com.example.ecosnap.models.WasteReportCard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class EcoSnapRepo(private val context: Context) {

    private val sharedPref = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()
    private val auth = FirebaseAuth.getInstance()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun postReportWaste(email: String, imageUri: String, wasteType: String, description: String,location: String): Result<Boolean> {
        val url = "https://eco-snap-server.vercel.app/report/create"
        val requestBody = mapOf(
            "user" to email,
            "imageUrl" to imageUri,
            "location" to location,
            "type" to wasteType,
            "description" to description
        )
        Log.d("EcoSnapDebug", "in main repo postReportWaste $requestBody")
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = JSONObject(requestBody).toString()
                val body = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("EcoSnapDebug", "in main repo postReportWaste response is successful}")
                    Result.success(true)
                } else {
                    Log.d("EcoSnapDebug", "in main repo postReportWaste response is not successful}")
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            } catch (e: IOException) {
                Log.d("EcoSnapDebug", "in main repo postReportWaste exception ${e.message}")
                Result.failure(e)
            }
        }
    }

    suspend fun fetchWasteReports(email: String): Result<List<WasteReportCard>> {
        val url = "https://eco-snap-server.vercel.app/report/from?userId=${email}"
        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("report") ?: JSONArray()
                    val wasteReports = mutableListOf<WasteReportCard>()
                    for (i in 0 until jsonArray.length()) {
                        val report = jsonArray.getJSONObject(i)
                        val id = report.getString("_id")
                        val user = report.getString("user")
                        val imageUrl = report.getString("imageUrl")
                        val location = report.getString("location")
                        val description = report.getString("description")
                        val status = report.getString("status")
                        val type = report.getString("type")
                        val createdAt = report.getString("createdAt")
                        val formatedDate = formatDate(createdAt)
                        val wasteReportCard = WasteReportCard(
                            id,
                            user,
                            imageUrl,
                            location,
                            description,
                            status,
                            type,
                            formatedDate
                        )
                        wasteReports.add(wasteReportCard)
                    }
                    Result.success(wasteReports)
                } else {
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(inputDate) ?: return ""

        val calendar = Calendar.getInstance().apply { time = date }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }

        val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        return "$day$suffix ${outputFormat.format(date)}"
    }

    suspend fun fetchLeaderboard(): Result<List<LeaderboardCard>> {
        val url = "https://eco-snap-server.vercel.app/user/points"
        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("users") ?: JSONArray()
                    val leaderboardCards = mutableListOf<LeaderboardCard>()
                    for (i in 0 until jsonArray.length()) {
                        val card = jsonArray.getJSONObject(i)
                        val rank = i+1
                        val name = card.getString("name")
                        val points = card.getInt("points")
                        val leaderboardCard = LeaderboardCard(
                            rank,
                            name,
                            points)
                        leaderboardCards.add(leaderboardCard)
                    }
                    Result.success(leaderboardCards)
                } else {
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
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

    suspend fun fetchUserDetails(email: String): Result<User> {
        val url = "https://eco-snap-server.vercel.app/user/profile?id=${email}"
        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONObject("user") ?: JSONObject()
                    val id = jsonArray.getString("_id")
                    val name = jsonArray.getString("name")
                    val email = jsonArray.getString("email")
                    val points = jsonArray.getInt("points")
                    val user = User(
                        id,
                        name,
                        email,
                        points
                        )
                    Result.success(user)
                }else{
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            }catch (e:Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun fetchAllEvents():Result<List<Event>> {
        val url = "https://eco-snap-server.vercel.app/event/all"
        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody)
                    val jsonArray = jsonObject.getJSONArray("events") ?: JSONArray()
                    val events = mutableListOf<Event>()
                    for (i in 0 until jsonArray.length()) {
                        val event = jsonArray.getJSONObject(i)
                        val title = event.getString("title")
                        val description = event.getString("description")
                        val date = event.getString("date")
                        val time = event.getString("time")
                        val location = event.getString("location")
                        val participants = event.getString("maxParticipants")
                        val points = event.getString("rewardPoints")
                        val organizer = event.getString("createdBy")
                        val eventt = Event(
                            title,
                            description,
                            date,
                            time,
                            location,
                            participants,
                            points,
                            organizer
                        )
                        events.add(eventt)
                    }
                    Result.success(events)
                } else {
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            }catch (e: Exception){
                Result.failure(e)
            }
        }
    }

    suspend fun postCleanImage(clickedReportId: String?, email: String, downloadUrl: String): Result<Boolean> {
        val url = "https://eco-snap-server.vercel.app/report/worker/update?id=$clickedReportId"
        Log.d("EcoSnapDebug", "in main repo postCleanImage $clickedReportId")
        val requestBody = mapOf(
            "user" to email,
            "imageUrl" to downloadUrl,
        )
        Log.d("EcoSnapDebug", "in main repo postReportWaste $requestBody")
        return withContext(Dispatchers.IO) {
            try {
                val jsonBody = JSONObject(requestBody).toString()
                val body = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody)
                val request = Request.Builder()
                    .url(url)
                    .put(body)
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("EcoSnapDebug", "in main repo postCleanImage response is successful}")
                    Result.success(true)
                } else {
                    Log.d("EcoSnapDebug", "in main repo postCleanImage response is not successful}")
                    Result.failure(Exception("API call failed with code: ${response.code}"))
                }
            } catch (e: IOException) {
                Log.d("EcoSnapDebug", "in main repo postCleanImage exception ${e.message}")
                Result.failure(e)
            }
        }
    }

}