package com.cursoudemy.gamelib.main

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.cursoudemy.gamelib.databinding.ActivityAddConsoleBinding
import com.cursoudemy.gamelib.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor, espere...")
        progressDialog.setCanceledOnTouchOutside(false)
        // On click listeners
        binding.btnBackForgot.setOnClickListener {
            onBackPressed()
        }
        binding.btnRecoverPass.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        email = binding.etEmailForgot.text.toString().trim()
        // Comprobar que no esté vacío
        if(email.isEmpty()) {
            Toast.makeText(this, "Debe introducir un email para poder enviarle instrucciones de recuperación...", Toast.LENGTH_SHORT).show()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Debe introducir un formato válido de email...", Toast.LENGTH_SHORT).show()
        }
        else {
            recoverPassword()
        }
    }

    private fun recoverPassword() {
        progressDialog.setMessage("Enviando correo de recuperación a $email")
        progressDialog.show()
        // Envío del email de recuperación
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Enviado correo de recuperación a $email", Toast.LENGTH_SHORT).show()
                clearField()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Envío de correo de recuperación a $email fallido", Toast.LENGTH_SHORT).show()
                clearField()
            }
    }

    private fun clearField() {
        binding.etEmailForgot.setText("")
    }
}