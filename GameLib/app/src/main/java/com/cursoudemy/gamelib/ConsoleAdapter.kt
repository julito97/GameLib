package com.cursoudemy.gamelib

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class ConsoleAdapter:RecyclerView.Adapter<ConsoleAdapter.HolderCategory>, Filterable {

    private val context: Context
    public var consoleArray: ArrayList<Console>
    private val filterList: ArrayList<Console>
    private var filter: FilterConsole? = null
    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, consoleArray: ArrayList<Console>) {
        this.context = context
        this.consoleArray = consoleArray
        this.filterList = consoleArray
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        // Inflate row category
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        // Get data, set data, handle click events
        // 1) Get data
        val model = consoleArray[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val time = model.timestamp
        // Set data
        binding.tvCategoryTitle.text = category
        // Delete category
        binding.btnDeleteCategory.setOnClickListener {
            // Confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Confirm") {a, d->
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)
                }
                .setNegativeButton("Cancel") { a, d->
                    a.dismiss()
                }
                .show()
        }
    }

    private fun deleteCategory(console: Console, holder: HolderCategory) {
        // We obtain the id of the console that will be deleted
        val id = console.id
        // We search in the db: root > categories > console id
        val aux = FirebaseDatabase.getInstance().getReference("Categories")
        aux.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int { // Size of the collection of categories
        return consoleArray.size
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tvCategory: TextView = binding.tvCategoryTitle
        var btnDelete: ImageButton = binding.btnDeleteCategory
    }

    override fun getFilter(): Filter {
        if(filter == null) {
            filter = FilterConsole(filterList, this)
        }

        return filter as FilterConsole
    }
}