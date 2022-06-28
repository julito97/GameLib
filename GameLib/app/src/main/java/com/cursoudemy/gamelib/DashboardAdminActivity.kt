package com.cursoudemy.gamelib

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import com.cursoudemy.gamelib.databinding.ActivityDashboardAdminBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    // Arraylist for the categories
    private lateinit var consoles: ArrayList<Console>
    // Adapter
    private lateinit var adapter: ConsoleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadConsoles()

        // Search function
        binding.etSearchConsole.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Called when the user types something
                try {
                        adapter.filter.filter(p0)
                }

                catch(e: Exception) {

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        // Add console button
        binding.btnAddConsole.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity, AddConsoleActivity::class.java))
        }
        // Add game button
        binding.btnAddGame.setOnClickListener {

        }
        // Log out
        binding.imgbtnLogoutDashboardAdmin.setOnClickListener {
            logOut()
            checkUser()
        }
    }

    private fun loadConsoles() { // To get the console list from the db
        // Initialize arraylist
        consoles = ArrayList()
        // Set up adapter
        adapter = ConsoleAdapter(this@DashboardAdminActivity, consoles)
        // Set adapter to recyclerview
        binding.rvConsoles.adapter = adapter
        // Get categories from the db: root > categories
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear list before start adding data into it
                consoles.clear()
                for(ds in snapshot.children) {
                    val consoleModel = ds.getValue(Console::class.java)
                    // Add to arraylist
                    consoles.add(consoleModel!!)
                    adapter.notifyDataSetChanged();
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun logOut() {
        firebaseAuth.signOut()
        checkUser()
    }

    private fun checkUser() {
        // Get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null) {
            // Not logged in, so it must redirect to the main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        else { //properly logged in; get and show user info
            val email = firebaseUser.email
            binding.tvEmailDashboardAdmin.text = email

        }
    }
}