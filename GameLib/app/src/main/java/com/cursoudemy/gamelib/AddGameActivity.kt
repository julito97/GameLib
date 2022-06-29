package com.cursoudemy.gamelib

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cursoudemy.gamelib.databinding.ActivityAddGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var consoles: ArrayList<Console>
    private var selectedConsoleId = ""
    private var selectedConsoleName = ""
    private var title = ""
    private var description = ""
    private var consoleName = ""
    // Show while submitting the info into the db
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadConsoles()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnAddGame.setOnClickListener {
            validateData()
        }

        binding.tvConsoleAddGame.setOnClickListener {
            consolePickDialog()
        }

        binding.btnBackAddGame.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadConsoles() {
        consoles = ArrayList()
        // Load the consoles from the db
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear before adding data
                consoles.clear()
                for(ds in snapshot.children) {
                    // Get data
                    val console = ds.getValue(Console::class.java)
                    // Add to the collection
                    consoles.add(console!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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

    private fun validateData() {
        // Get data; description can be empty
        title = binding.etGameTitleAddGame.text.toString().trim()
        description = binding.etGameDescriptionAddGame.text.toString().trim()
        selectedConsoleName = binding.tvConsoleAddGame.text.toString().trim()
        // Validation
        if(title.isEmpty()) {
            Toast.makeText(this, "You have to introduce a name for the game", Toast.LENGTH_SHORT).show()
        }
        else if(selectedConsoleName.isEmpty()) {
            Toast.makeText(this, "You have to introduce a console for the game", Toast.LENGTH_SHORT).show()
        }
        else { // Data validated
            progressDialog.setMessage("Adding game to your collection...")
            progressDialog.show()
            addGame()
        }
    }

    private fun addGame() {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()
        // Add id, title, console, timestamp, uid
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["console"] = consoleName
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        // Ad to db: Root -> Consoles -> consoleId -> console info
        val aux = FirebaseDatabase.getInstance().getReference("Games")
        aux.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Game added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Error while adding the new game: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}