package com.cursoudemy.gamelib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cursoudemy.gamelib.databinding.ActivityDashboardAdminBinding

import com.google.firebase.auth.FirebaseAuth

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        // Add console button
        binding.btnAddConsole.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity, AddConsoleActivity::class.java))
        }
        // Add game button
        binding.btnAddGame.setOnClickListener {

        }
        // Log out
        binding.imgbtnLogoutDashboardAdmin.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        firebaseAuth.signOut()
        checkUser()
    }

    private fun checkUser() {
        // Get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            // Not logged in, so it must redirect to the main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        else { //properly logged in; get and show user info
            val email = firebaseUser.email
            binding.tvEmailDashboardAdmin.text = email

        }
    }
}