package com.cursoudemy.gamelib.main

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.adapters.GameAdapter
import com.cursoudemy.gamelib.databinding.ActivityGameListBinding
import com.cursoudemy.gamelib.edition.EditGameActivity
import com.cursoudemy.gamelib.models.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.random.Random

class GameListActivity : AppCompatActivity(), GameAdapter.ItemClickListener {

    private lateinit var binding: ActivityGameListBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    // Arraylist for the games
    private var games = arrayListOf<Game>()
    private var copyList = games.clone() as ArrayList<Game>
    private var adapter = GameAdapter(games, this)
    var consoleName = ""
    var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionbar
        val actionbar = supportActionBar
        // Título de la action bar
        actionbar!!.title = "Game list"
        // Botón de retorno a pantalla anterior
        actionbar.setDisplayHomeAsUpEnabled(true)
        // Nombre de la consola para filtrar los juegos mostrados
        consoleName = intent?.extras?.get("console") as String
        // Inicialización de la base de datos
        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.currentUser!!.uid
        // Inflado del recyclerview y carga de los juegos según consola seleccionada y usuario actual
        setUpRecyclerView()
        checkUser()
        loadGames(uid)

        // Función de búsqueda
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

        binding.tvRandomGame.setOnClickListener {
            if(games.isEmpty()) {
                Toast.makeText(this, "Debes añadir juegos para usar esta funcionalidad", Toast.LENGTH_SHORT).show()
            }
            else {
                var gamesSize = games.size
                val chosenGame = Random.nextInt(gamesSize)
                binding.tvRandomGame.setText("Puedes empezar a jugar a " + games[chosenGame].title)
            }
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

    private fun loadGames(uid: String) {
        // Get games from the db: root > Games
        val aux = FirebaseDatabase.getInstance().getReference("Games")
        aux.orderByChild("console").equalTo(consoleName)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    games.clear()
                    for (ds in snapshot.children) {
                        val modelGame = ds.getValue(Game::class.java)
                        if(modelGame!!.uid == uid) {
                            games.add(modelGame!!)
                        }
                        copyList = games.clone() as ArrayList<Game>
                        adapter.notifyDataSetChanged();
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

    override fun onResume() {
        super.onResume()
        loadGames(uid)
    }

    override fun onClickItem(game: Game) {
        var gameForOptions = game
        val builder = AlertDialog.Builder(this@GameListActivity)
        builder.setTitle("Select an option")
        builder.setPositiveButton("Edit") { dialog, which ->
            // Edit the selected game
            val intent = Intent(this, EditGameActivity::class.java)
            // Put extras
            intent.putExtra("title", gameForOptions.title)
            //intent.putExtra("id", gameForOptions.id)
            // Init activity
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Delete") { dialog, which ->
            // Delete element from db and adapter
            deleteGame(gameForOptions)
        }
        builder.show()
    }

    private fun deleteGame(game: Game) {
        val id = game.title
        val aux = FirebaseDatabase.getInstance().getReference("Games")
        aux.child(id).ref.removeValue().addOnSuccessListener {
            Toast.makeText(applicationContext, "The console was successfully deleted", Toast.LENGTH_SHORT).show()
            // update the UI
            games.remove(game)
            adapter.notifyDataSetChanged()
        }
            .addOnFailureListener { e->
                Toast.makeText(applicationContext, "Error while deleting: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
    }) */
    }

