package com.cursoudemy.gamelib

import android.widget.Filter

class ConsoleFilter: Filter {

    private var filteredList: ArrayList<Console>
    private var consoleAdapter: ConsoleAdapter

    constructor(filterList: ArrayList<Console>, consoleAdapter: ConsoleAdapter) : super() {
        this.filteredList = filterList
        this.consoleAdapter = consoleAdapter
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()
        // Check that the value of the text input isn't null or empty
        if(constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().toUpperCase()
            val filteredConsole: ArrayList<Console> = ArrayList()
            for(i in 0 until filteredConsole.size) {
                // Validation
                if(filteredList[i].category.uppercase().contains(constraint)) {
                    // Add to filtered list
                    filteredConsole.add(filteredList[i])
                }
            }

            results.count = filteredConsole.size
            results.values = filteredConsole
        }

        else {
            // Search value is empty or null
            results.count = filteredList.size
            results.values = filteredList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        // Apply filter changes
        consoleAdapter.consoleArraylist = results.values as ArrayList<Console>
        consoleAdapter.notifyDataSetChanged()
    }
}