package com.cursoudemy.gamelib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cursoudemy.gamelib.databinding.ActivityDashboardAdminBinding
import com.cursoudemy.gamelib.databinding.ActivityDashboardUserBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardUserBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        // Log out
        binding.imgbtnLogoutDashboardUser.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        firebaseAuth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun checkUser() {
        // Get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            // Not logged in, but the user can stay
            binding.tvEmailDashboardUser.text = "Not logged in"
        }

        else { //properly logged in; get and show user info
            val email = firebaseUser.email
            binding.tvEmailDashboardUser.text = email

        }
    }
}