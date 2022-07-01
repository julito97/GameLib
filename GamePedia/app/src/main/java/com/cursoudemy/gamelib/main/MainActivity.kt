package com.cursoudemy.gamelib.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cursoudemy.gamelib.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        // cuando pulsamos iniciar sesión
        mBinding.btnLoginMain.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        //cuando debemos registrarnos
        mBinding.btnSignInMain.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}


/*
* REQUERIDO: enviar el email a la siguiente actividad o chequear usando Firebase
* */