package com.cursoudemy.gamelib

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cursoudemy.gamelib.databinding.RowGameBinding

class GameAdapter (private var games: List<Game>, val itemClickListener: GameAdapter.ItemClickListener): RecyclerView.Adapter<GameAdapter.ViewHolder>()  {

    private lateinit var context: Context

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = RowGameBinding.bind(itemView)

        fun bind(game: Game) {
            val game = game
            val id = game.id
            val title = game.title
            val status = game.status
            val description = game.description
            val console = game.console
            val timestamp = game.timestamp
            // Set data
            binding.tvGameTitle.text = title
            binding.tvGameConsole.text = console
            binding.tvGameDescription.text = description
            binding.tvGameStatus.text = status
            // Handle click to edit a game
            binding.GameRow.setOnClickListener() {
                itemClickListener.onClickItem(game)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_game, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return games.size
    }

    interface ItemClickListener{
        fun onClickItem(game: Game)
    }

}

