package com.example.ecosnap.auth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.ecosnap.auth.repositories.AuthRepo
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepo(application.applicationContext)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> = _authResult

    private val _userSavedInBackend = MutableLiveData<Result<Boolean>>()
    val userSavedInBackend: LiveData<Result<Boolean>> = _userSavedInBackend



    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }


    fun signInWithEmail(email: String, password: String,isWorker: Boolean) {
        setLoading(true)
        viewModelScope.launch {
            val result = authRepo.signInWithEmailPassword(email, password,isWorker)
            _authResult.postValue(result)
            setLoading(false)
        }
    }

    fun getIsWorker() : Boolean {
        return authRepo.getIsWorker()
    }

    fun signUpWithEmail(name: String,email: String, password: String) {
        setLoading(true)
        viewModelScope.launch {
            val result = authRepo.signUpWithEmailPassword(name, email, password)
            _authResult.postValue(result)
            setLoading(false)
        }
    }

    fun saveUserToBackend(name: String, email: String,password: String) {
        setLoading(true)
        viewModelScope.launch {
            val result = authRepo.saveUserToBackend(name, email,password)
            _userSavedInBackend.postValue(result)
            setLoading(false)
        }
    }


    fun logout(){
        authRepo.logout()
    }
}