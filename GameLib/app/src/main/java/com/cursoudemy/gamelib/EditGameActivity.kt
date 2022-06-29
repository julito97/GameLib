package com.cursoudemy.gamelib

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cursoudemy.gamelib.databinding.ActivityEditGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditGameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var gameTitle = ""
    private var selectedConsoleName = ""
    private var consoles: ArrayList<Console> = arrayListOf<Console>()
    private var copyList = consoles.clone() as java.util.ArrayList<Console>
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Edit game"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        // Get the game title to edit said game
        gameTitle = intent?.extras?.get("title") as String
        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        // Build progress bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        // Load the game info into the fields
        loadEditInfo()

        binding.btnBackEditGame.setOnClickListener {
            onBackPressed()
        }

        binding.btnEditGame.setOnClickListener {
            // Check that the title field isn't empty
            validateFields()
        }

    }

    private fun validateFields() {
        TODO("Not yet implemented")
    }

    private fun loadEditInfo() {
        // Search for the game in the database
        loadConsoles()
    }

    private fun loadConsoles() {
        // Load consoles to put them into the textfield
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear list before start adding data into it
                consoles.clear()
                for(ds in snapshot.children) {
                    val consoleModel = ds.getValue(Console::class.java)
                    // Add to arraylist
                    consoles.add(consoleModel!!)
                    copyList = consoles.clone() as java.util.ArrayList<Console>
                    //mAdapter.notifyDataSetChanged();
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}