package com.cursoudemy.gamelib

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.databinding.RowConsoleBinding

class ConsoleAdapter(private val consoles: List<Console>, val itemClickListener: ItemClickListener, val textClickListener: TextClickListener):
    RecyclerView.Adapter<ConsoleAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = RowConsoleBinding.bind(itemView)

        fun bind(console: Console) {
            val console = console
            val id = console.id
            val consoleName = console.console
            val timestamp = console.timestamp
            // Set data
            binding.tvConsoleTitle.text = consoleName
            // Handle click to delete a console
            binding.btnDeleteConsole.setOnClickListener {
                itemClickListener.onClickItem(console)
            }
            binding.tvConsoleTitle.setOnClickListener {
                textClickListener.onClickText(consoleName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_console, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(consoles[position])
    }

    override fun getItemCount(): Int = consoles.size

    interface ItemClickListener{
        fun onClickItem(console: Console)
    }

    interface TextClickListener{
        fun onClickText(console: String)
    }
}