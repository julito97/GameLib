package com.cursoudemy.gamelib

import android.app.AlertDialog
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
    private var selectedConsoleId = ""
    private var consoleName = ""
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
        loadConsoles()

        binding.btnBackEditGame.setOnClickListener {
            onBackPressed()
        }

        binding.btnEditGame.setOnClickListener {
            // Check that the title field isn't empty
            validateFields()
        }

        binding.tvConsoleAddGame.setOnClickListener {
            consolePickDialog()
        }

    }

    private fun consolePickDialog() {
        // Get string array of the consoles from arraylist
        val consolesArray = arrayOfNulls<String>(consoles.size)
        for(i in consolesArray.indices) {
            consolesArray[i] = consoles[i].console
        }
        // Alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick a console")
            .setItems(consolesArray) { dialog, which ->
                // Handle item click and get clicked item
                selectedConsoleName = consoles[which].console
                selectedConsoleId = consoles[which].id
                // Set console to textview
                binding.tvConsoleAddGame.text = selectedConsoleName
                consoleName = selectedConsoleName
            } .show()
    }

    private fun validateFields() {
        //Checks that all the fields aren't empty
    }

    private fun loadEditInfo() {
        // Search for the game in the database using the title
        val ref =
        binding.etGameTitleEditGame
        binding.etGameDescriptionEditGame
        binding.etGameStatusEditGame
        binding.btnEditGame
        binding.tvConsoleAddGame
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
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}