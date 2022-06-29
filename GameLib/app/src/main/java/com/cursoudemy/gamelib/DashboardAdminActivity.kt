package com.cursoudemy.gamelib

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.ConsoleAdapter.ItemClickListener
import com.cursoudemy.gamelib.databinding.ActivityDashboardAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class DashboardAdminActivity : AppCompatActivity(), ItemClickListener,
    ConsoleAdapter.TextClickListener {
    private lateinit var binding: ActivityDashboardAdminBinding
    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    // Arraylist for the consoles
    private var consoles = arrayListOf<Console>()
    private var copyList = consoles.clone() as ArrayList<Console>
    // Adapter
    private var mAdapter = ConsoleAdapter(consoles, this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        setUpRecyclerView()
        checkUser()
        loadConsoles()


        // Search function
        binding.etSearchConsole.doOnTextChanged { text, _, _, _ ->
            val filteredList = arrayListOf<Console>()
            consoles.clear()
            if (!text.isNullOrBlank()) {
                val filterPattern = text.toString().lowercase(Locale.getDefault())
                for (item in copyList) {
                    if (item.console.lowercase(Locale.getDefault()).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
                consoles.addAll(filteredList)
            } else {
                consoles.addAll(copyList)
            }
            mAdapter.notifyDataSetChanged()
        }
        // Add console button
        binding.btnAddConsole.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity, AddConsoleActivity::class.java))
        }
        // Add game button
        binding.btnAddGame.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity, AddGameActivity::class.java))

        }
        // Log out
        binding.imgbtnLogoutDashboardAdmin.setOnClickListener {
            logOut()
            checkUser()
        }
    }

    private fun setUpRecyclerView() {
        binding.rvConsoles.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        binding.rvConsoles.adapter = mAdapter
    }

    private fun loadConsoles() { // To get the console list from the db
        // Get categories from the db: root > consoles
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear list before start adding data into it
                consoles.clear()
                for(ds in snapshot.children) {
                    val consoleModel = ds.getValue(Console::class.java)
                    // Add to arraylist
                    consoles.add(consoleModel!!)
                    copyList = consoles.clone() as ArrayList<Console>
                    mAdapter.notifyDataSetChanged();
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

    override fun onClickItem(console: Console) {
        // here we make add the logic to remove item
        val builder = AlertDialog.Builder(this@DashboardAdminActivity)
        builder.setTitle("Delete").setMessage("Are you sure?")
            .setPositiveButton("Confirm") { a, d->
                Toast.makeText(applicationContext, "Deleting...", Toast.LENGTH_SHORT).show()
                deleteConsole(console)
            }
            .setNegativeButton("Cancel") { a, d->
                a.dismiss()
            }
            .show()
    }

    override fun onClickText(console: String) {
        // Open activity with the games from that console
        val intent = Intent(this, GameListActivity::class.java)
        // Put extras
        intent.putExtra("console", console)
        // Init activity
        startActivity(intent)
    }

    private fun deleteConsole(console: Console) {
        // Get the id of the item that will be deleted: root > consoles > id
        val id = console.id
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.child(id).ref.removeValue().addOnSuccessListener {
            Toast.makeText(applicationContext, "The console was successfully deleted", Toast.LENGTH_SHORT).show()
            // update the UI
            consoles.remove(console)
            mAdapter.notifyDataSetChanged()
        }
            .addOnFailureListener { e->
                Toast.makeText(applicationContext, "Error while deleting: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}