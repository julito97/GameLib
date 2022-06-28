package com.cursoudemy.gamelib

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.databinding.RowConsoleBinding
import com.google.android.material.transition.Hold
//import com.cursoudemy.gamelib.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class ConsoleAdapter: RecyclerView.Adapter<ConsoleAdapter.HolderCategory>, Filterable {

    private val context: Context
    public var consoleArraylist: ArrayList<Console>
    private lateinit var binding: RowConsoleBinding
    private var filterList: ArrayList<Console>
    private var filter: ConsoleFilter? = null

    // Constructor
    constructor(context: Context, consoleArraylist: ArrayList<Console>) {
        this.context = context
        this.consoleArraylist = consoleArraylist
        this.filterList = consoleArraylist
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding = RowConsoleBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        // Get data, set data, handle clicks, etc
        val console = consoleArraylist[position]
        val id = console.id
        val consoleName = console.console
        val timestamp = console.timestamp
        // Set data
        holder.tvConsole.text = consoleName
        // Handle click to delete a console
        binding.btnDeleteConsole.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete").setMessage("Are you sure?")
                .setPositiveButton("Confirm") { a, d->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteConsole(console, holder)
                }
                .setNegativeButton("Cancel") { a, d->
                    a.dismiss()
                }
                .show()
        }
    }

    private fun deleteConsole(console: Console, holder: HolderCategory) {
        // Get the id of the item that will be deleted: root > consoles > id
        val id = console.id
        val aux = FirebaseDatabase.getInstance().getReference("Consoles")
        aux.child("id").removeValue().addOnSuccessListener {
            Toast.makeText(context, "The console was successfully deleted", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e->
                Toast.makeText(context, "Error while deleting: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int { // Returns the number of items in the list
        return consoleArraylist.size
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) { // To init views for row_console.xml
        // Init ui views
        var tvConsole: TextView = binding.tvConsoleTitle
        var btnDelete: ImageButton = binding.btnDeleteConsole
    }

    override fun getFilter(): Filter {
        if(filter == null) {
            filter = ConsoleFilter(filterList, this)
        }

        return filter as ConsoleFilter
    }
}