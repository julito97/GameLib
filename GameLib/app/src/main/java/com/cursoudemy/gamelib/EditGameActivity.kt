package com.cursoudemy.gamelib

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cursoudemy.gamelib.databinding.ActivityEditGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class EditGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditGameBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var gameTitle = "" // Esto nos permitirá obtener el juego a editar de la base de datos
    // Variables para cargar la lista de consolas
    private var selectedConsoleName = ""
    private var selectedConsoleId = ""
    private var consoleName = ""
    private var consoles: ArrayList<Console> = arrayListOf<Console>()
    private var copyList = consoles.clone() as java.util.ArrayList<Console>
    // Para mostrar la lista de consolas y elegir una
    private lateinit var progressDialog: ProgressDialog
    // Variables para poder editar el juego
    var gameTitleEdit = ""
    var gameId = ""
    var descriptionEdit = ""
    var statusEdit = ""
    var consoleEdit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Edit game"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(false)
        // Get the game title to edit said game
        gameTitle = intent?.extras?.get("title") as String
        //gameId = intent?.extras?.get("id") as String
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
        // Comprueba que los campos requeridos no estén vacíos
        if(binding.etGameTitleEditGame.text.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
        // El comentario puede estar vacío; no es obligatorio
        else if(binding.etGameStatusEditGame.text.isEmpty()) {
            Toast.makeText(applicationContext, "El status no puede estar vacío", Toast.LENGTH_SHORT).show()
        }
        else if(binding.tvConsoleAddGame.text.isEmpty()) {
            Toast.makeText(applicationContext, "Tienes que asignar una consola al juego", Toast.LENGTH_SHORT).show()
        }
        else { // Si no lo están, se borra el juego y se crea de nuevo
            updateGame()
        }
        /*
        aux.child(id).ref.removeValue().addOnSuccessListener {
            Toast.makeText(applicationContext, "The console was successfully deleted", Toast.LENGTH_SHORT).show()
            // update the UI
            consoles.remove(console)
            mAdapter.notifyDataSetChanged()
        }
            .addOnFailureListener { e->
                Toast.makeText(applicationContext, "Error while deleting: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        */
    }

    private fun updateGame() {
        val timestamp = System.currentTimeMillis()
        //
        gameTitleEdit = binding.etGameTitleEditGame.text.toString().trim()
        descriptionEdit = binding.etGameDescriptionEditGame.text.toString().trim()
        statusEdit = binding.etGameStatusEditGame.text.toString().trim()
        consoleName = binding.tvConsoleAddGame.text.toString().trim()

        val hashMap = HashMap<String, Any>()
        hashMap["console"] = consoleName
        hashMap["description"] = descriptionEdit
        hashMap["id"] = timestamp
        hashMap["status"] = statusEdit
        //hashMap["timestamp"] = timestamp
        hashMap["title"] = gameTitleEdit
        hashMap["uid"] = "${firebaseAuth.uid}"

        val aux = FirebaseDatabase.getInstance().getReference("Games")
        aux.child(gameTitle) //
            .updateChildren(hashMap).addOnSuccessListener {
                Toast.makeText(this, "Juego editado exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al actualizar el juego: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        /*
        aux.child(gameTitleEdit).child("console").setValue(hashMap["description"])
        aux.child(gameTitleEdit).child("description").setValue(hashMap["description"])
        aux.child(gameTitleEdit).child("id").setValue(hashMap["id"])
        aux.child(gameTitleEdit).child("timestamp").setValue(hashMap["timestamp"])
        aux.child(gameTitleEdit).child("title").setValue(hashMap["title"])
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Juego editado exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al actualizar el juego: ${e.message}", Toast.LENGTH_SHORT).show()
            } */
    }

    private fun loadEditInfo() {
        // Search for the game in the database using the title
        val ref = FirebaseDatabase.getInstance().getReference("Games")
        ref.child(gameTitle).ref.addListenerForSingleValueEvent(object:ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var gameTitleEdit = gameTitle

                binding.etGameTitleEditGame.setText(gameTitleEdit)
                loadConsoles()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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

    private fun clearFields() {
        binding.etGameTitleEditGame.setText("")
        binding.etGameDescriptionEditGame.setText("")
        binding.etGameStatusEditGame.setText("")
        binding.tvConsoleAddGame.text = "Console"
    }

}