package com.cursoudemy.gamelib

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cursoudemy.gamelib.databinding.ActivityAddConsoleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddConsoleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddConsoleBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var console = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddConsoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnBackAddConsole.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddConsole.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        console = binding.etAddConsole.text.toString().trim()

        if(console.isEmpty()) {
            Toast.makeText(this, "You have to enter a console name", Toast.LENGTH_SHORT).show()
        }

        else {
            addConsole()
        }
    }

    private fun addConsole() {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["console"] = console
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"
        // Ad to db: Root -> Consoles -> consoleId -> console info
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Console added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Error while adding the new console: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}