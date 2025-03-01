package com.example.ecosnap.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ecosnap.models.Event
import com.example.ecosnap.models.LeaderboardCard
import com.example.ecosnap.models.User
import com.example.ecosnap.models.WasteReportCard
import com.example.ecosnap.repository.EcoSnapRepo
import kotlinx.coroutines.launch

class MainViewmodel(application: Application): AndroidViewModel(application) {
    private val mainRepo = EcoSnapRepo(application.applicationContext)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isReportWastePosted = MutableLiveData<Result<Boolean>>()
    val isReportWastePosted: LiveData<Result<Boolean>> = _isReportWastePosted


    private val _isCleanReportWastePosted = MutableLiveData<Result<Boolean>>()
    val isCleanReportWastePosted: LiveData<Result<Boolean>> = _isCleanReportWastePosted

    private val _wasteReports = MutableLiveData<Result<List<WasteReportCard>>>()
    val wasteReports: LiveData<Result<List<WasteReportCard>>> = _wasteReports

    private val _fetchLeaderboard = MutableLiveData<Result<List<LeaderboardCard>>>()
    val fetchLeaderboard: LiveData<Result<List<LeaderboardCard>>> = _fetchLeaderboard

    private val _fetchUserDetail = MutableLiveData<Result<User>>()
    val fetchUserDetail: LiveData<Result<User>> = _fetchUserDetail

    private val _fetchAllEvents = MutableLiveData<Result<List<Event>>>()
    val fetchAllEvents: LiveData<Result<List<Event>>> = _fetchAllEvents


    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }


    fun postReportWaste(email: String, imageUri: String, wasteType: String, description: String,location: String){
        _isLoading.value = true
        viewModelScope.launch {
            val result = mainRepo.postReportWaste(email, imageUri, wasteType, description,location)
            _isReportWastePosted.postValue(result)
            _isLoading.value = false
        }
    }

    fun fetchWasteReports(email: String){
        _isLoading.value = true
        viewModelScope.launch {
            val result = mainRepo.fetchWasteReports(email)
            _wasteReports.postValue(result)
            _isLoading.value = false
        }
    }

    fun fetchUserDetails(email: String){
        _isLoading.value = true
        viewModelScope.launch {
            val result = mainRepo.fetchUserDetails(email)
            _fetchUserDetail.postValue(result)
            _isLoading.value = false
        }
    }

    fun fetchAllEvents(){
        _isLoading.value = true
        viewModelScope.launch {
            val result = mainRepo.fetchAllEvents()
            _fetchAllEvents.postValue(result)
            _isLoading.value = false
        }
    }

    fun fetchLeaderboard(){
        _isLoading.value = true
        viewModelScope.launch {
            val result = mainRepo.fetchLeaderboard()
            _fetchLeaderboard.postValue(result)
            _isLoading.value = false
        }
    }

    fun logout(){
        mainRepo.logout()
    }

    fun postCleanImage(clickedReportId: String?, email: String, downloadUrl: String) {
        viewModelScope.launch {
            val result = mainRepo.postCleanImage(clickedReportId, email, downloadUrl)
            _isCleanReportWastePosted.postValue(result)
        }
    }


}