package com.cursoudemy.gamelib

import android.widget.Filter

class FilterConsole: Filter {
    // Arraylist in which we want to search
    private lateinit var filterList: ArrayList<Console>
    // Adapter
    private lateinit var consoleAdapter: ConsoleAdapter

    constructor(filterList: ArrayList<Console>, categoryAdapter: ConsoleAdapter) : super() {
        this.filterList = filterList
        this.consoleAdapter = categoryAdapter
    }


    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val result = FilterResults()
        // Value shouldn't be null or empty
        if(constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filteredModel: ArrayList<Console> = ArrayList()
                for(i in 0 until filterList.size) {
                    // Validation
                    if(filterList[i].category.uppercase().contains(constraint)) {
                        // Add to filtered list
                        filteredModel.add(filterList[i])
                    }
                }

            result.count = filteredModel.size
            result.values = filteredModel
        }

        else {
            // Search value is null or empty
            result.count = filterList.size
            result.values = filterList
        }

        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        // Apply filter changes
        consoleAdapter.consoleArraylist = results.values as ArrayList<Console>

    }
}