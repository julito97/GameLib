package com.cursoudemy.gamelib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.databinding.ActivityGameListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class GameListActivity : AppCompatActivity(), GameAdapter.ItemClickListener {

    private lateinit var binding: ActivityGameListBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    // Arraylist for the games
    private var games = arrayListOf<Game>()
    private var copyList = games.clone() as ArrayList<Game>
    private var adapter = GameAdapter(games, this)
    var consoleName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Game list"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        // Get the console name to filter the games
        consoleName = intent?.extras?.get("console") as String
        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        //
        setUpRecyclerView()
        checkUser()
        loadGames()

        //


        // Search function
        binding.etSearchGame.doOnTextChanged { text, _, _, _ ->
            val filteredList = arrayListOf<Game>()
            games.clear()
            if (!text.isNullOrBlank()) {
                val filterPattern = text.toString().lowercase(Locale.getDefault())
                for (item in copyList) {
                    if (item.title.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
                games.addAll(filteredList)
            } else {
                games.addAll(copyList)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView() {
        binding.rvGames.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        binding.rvGames.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            // Not logged in, so it must redirect to the main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else { //properly logged in; don't show anything this time
        }
    }

    private fun loadGames() {
        // Get categories from the db: root > Games
        val aux = FirebaseDatabase.getInstance().getReference("Games")
        aux.orderByChild("console").equalTo(consoleName)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    games.clear()
                    for (ds in snapshot.children) {
                        val modelGame = ds.getValue(Game::class.java)
                        games.add(modelGame!!)
                        copyList = games.clone() as ArrayList<Game>
                        adapter.notifyDataSetChanged();
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

    override fun onClickItem(game: Game) {
        TODO("Not yet implemented")
    }

    /* TO LOAD ALL GAMES
    aux.addValueEventListener(object: ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Clear list before start adding data into it
            games.clear()
            for(ds in snapshot.children) {
                val gameModel = ds.getValue(Game::class.java)
                // Add to arraylist
                games.add(gameModel!!)
                copyList = games.clone() as ArrayList<Game>
                adapter.notifyDataSetChanged();
            }
        }
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }) */
    }

