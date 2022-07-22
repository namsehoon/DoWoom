package com.example.dowoom.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dowoom.R
import com.example.dowoom.databinding.ItemGameBinding
import com.example.dowoom.model.gameModel.GameModel

class GameAdapter(val context: Context,val goIntoGameRoom:(GameModel) -> Unit) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {


    var games = mutableListOf<GameModel>()

    // 사다리게임
    val LADDER_GAME = "사다리게임"
    // 돌림판
    val CIRCLE_GAME = "돌려돌려 돌림판"
    // 선착순게임
    val FASTER_GAME = "선착순게임"

    fun setGameRoom(Games:MutableList<GameModel>) {
        Log.d("abcd","games is :"+Games.toString())
        games.clear()
        games.addAll(Games)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameAdapter.GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game,parent,false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameAdapter.GameViewHolder, position: Int) {
        val game = games[position]

        holder.gameBinding.tvCreatorName.text = game.nickname
        holder.gameBinding.tvLeftCount.text = game.leftCount.toString()
        holder.gameBinding.tvAcceptable.text = game.acceptable.toString()
        holder.gameBinding.tvGameTitle.text = game.title


        when(game.whatKindGame) {
            0 -> holder.gameBinding.tvWhatGame.text = LADDER_GAME
            1 -> holder.gameBinding.tvWhatGame.text = LADDER_GAME
            2 -> holder.gameBinding.tvWhatGame.text = CIRCLE_GAME
            3 -> holder.gameBinding.tvWhatGame.text = FASTER_GAME
        }

        holder.gameBinding.llGame.setOnClickListener {
            goIntoGameRoom(game)
        }

        holder.gameBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return games.size
    }

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var gameBinding: ItemGameBinding = ItemGameBinding.bind(itemView)
    }
}