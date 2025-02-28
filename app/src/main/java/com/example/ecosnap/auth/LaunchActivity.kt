package com.example.ecosnap.auth

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.ecosnap.activities.MainActivity
import com.example.ecosnap.R
import com.example.ecosnap.auth.fragments.AuthFragment
import com.example.ecosnap.databinding.ActivityLauchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LaunchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauchBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        forceLightTheme()
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val authFragment = AuthFragment()

        if (currentUser != null) {
            navigateToMainActivity()
        } else {
            navigateToAuthFragment(authFragment)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAuthFragment(authFragment: AuthFragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.auth_fragments,authFragment)
            commit()
        }
    }
    private fun forceLightTheme() {
        if (this.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}