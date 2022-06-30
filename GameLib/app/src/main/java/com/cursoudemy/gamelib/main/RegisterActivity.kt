package com.cursoudemy.gamelib.main

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.cursoudemy.gamelib.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    // Viewbinding
    private lateinit var rBinding: ActivityRegisterBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    // Progress Dialog;
    private lateinit var progressDialog: ProgressDialog
    // For the authentication process
    private var name = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(rBinding.root)
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize progress dialog; will show up while logging in/registering
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        // Handle the back button
        rBinding.btnBackRegister.setOnClickListener {
            onBackPressed()
        }
        // Handle click; beginning of the registering process
        rBinding.btnRegisterRegister.setOnClickListener {
            validateData()
        }

    }

    private fun validateData() {
        // Input data
        name = rBinding.etNameRegister.text.toString().trim()
        email = rBinding.etEmailRegister.text.toString().trim()
        password = rBinding.etPasswordRegister.text.toString().trim()
        val cPassword = rBinding.etPasswordConfirmRegister.text.toString().trim()
        // Validate data
        if(name.isEmpty())
        {
            Toast.makeText(this, "Introduce a name.", Toast.LENGTH_SHORT).show()
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Introduce a correct email format.", Toast.LENGTH_SHORT).show()
        }

        else if(password.isEmpty())
        {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show()
        }

        else if(cPassword.isEmpty())
        {
            Toast.makeText(this, "Please confirm your password.", Toast.LENGTH_SHORT).show()
        }

        else if(password != cPassword)
        {
            Toast.makeText(this, "The password doesn't match the confirm password you introduced.", Toast.LENGTH_SHORT).show()
        }

        else {
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating account...")
        progressDialog.show()
        // Create user in Firebase Auth
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { // If it can create the account, it will add the user info on the db
            updateUserInfo()
        }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Account creation failed: ${e.message}.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        // Save user info
        progressDialog.setMessage("Creating account...")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid
        // Set up data to add everything into the db
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["pfp"] = "" // Empty
        hashMap["timestamp"] = timestamp
        hashMap["userType"] = "admin"
        // Set data to db
        val aux = FirebaseDatabase.getInstance().getReference("Users")
        aux.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Account info saving done correctly.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardAdminActivity::class.java))
                finish()

            }
            .addOnFailureListener { e->
                Toast.makeText(this, "Account info saving failed: ${e.message}.", Toast.LENGTH_SHORT).show()
            }
    }
}