package com.cursoudemy.gamelib.main

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.cursoudemy.gamelib.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    // Progress Dialog;
    private lateinit var progressDialog: ProgressDialog
    // For the authentication process
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize progress dialog; will show up while logging in/registering
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        // If you have no account, you have to register
        binding.tvNoAccountLogin.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        // If you do have an account, the login process begins
        binding.btnLoginLogin.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        // Input data
        email = binding.etEmailLogin.text.toString().trim()
        password = binding.etPasswordLogin.text.toString().trim()
        // Validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
        }

        else if(password.isEmpty()) {
            Toast.makeText(this, "You have to introduce a password.", Toast.LENGTH_SHORT).show()
        }

        else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Loging in...")
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun checkUser() { // If user, move to user dashboard; if admin, move to admin dashboard
        progressDialog.setMessage("Checking the user...")
        val firebaseUser = firebaseAuth.currentUser!!
        val aux = FirebaseDatabase.getInstance().getReference("Users")
        aux.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}