package com.cursoudemy.gamelib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GameListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Game list"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}