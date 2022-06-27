package com.cursoudemy.gamelib

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.databinding.RowCategoryBinding

class CategoryAdapter:RecyclerView.Adapter<CategoryAdapter.HolderCategory> {

    private val context: Context
    private val categoryArray: ArrayList<Category>
    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArray: ArrayList<Category>) {
        this.context = context
        this.categoryArray = categoryArray
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tvCategory: TextView = binding.tvCategoryTitle
        var btnDelete: ImageButton = binding.btnDeleteCategory
    }
}