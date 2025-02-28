package com.example.ecosnap.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ecosnap.models.LeaderboardCard
import com.example.ecosnap.models.WasteReportCard
import com.example.ecosnap.repository.EcoSnapRepo
import kotlinx.coroutines.launch

class MainViewmodel(application: Application): AndroidViewModel(application) {
    private val mainRepo = EcoSnapRepo(application.applicationContext)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isReportWastePosted = MutableLiveData<Result<Boolean>>()
    val isReportWastePosted: LiveData<Result<Boolean>> = _isReportWastePosted

    private val _wasteReports = MutableLiveData<Result<List<WasteReportCard>>>()
    val wasteReports: LiveData<Result<List<WasteReportCard>>> = _wasteReports

    private val _fetchLeaderboard = MutableLiveData<Result<List<LeaderboardCard>>>()
    val fetchLeaderboard: LiveData<Result<List<LeaderboardCard>>> = _fetchLeaderboard

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }


    fun postReportWaste(email: String, imageUri: String, wasteType: String, description: String){
        _isLoading.value = true
        viewModelScope.launch {
            val result = mainRepo.postReportWaste(email, imageUri, wasteType, description)
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


}