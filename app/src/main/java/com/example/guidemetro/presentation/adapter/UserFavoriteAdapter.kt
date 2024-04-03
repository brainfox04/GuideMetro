package com.example.guidemetro.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guidemetro.R
import com.example.guidemetro.UserFavorite

class UserFavoriteAdapter(private var userFavorites: MutableList<UserFavorite>) : RecyclerView.Adapter<UserFavoriteAdapter.UserFavoriteViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(favorite: UserFavorite)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    interface OnRemoveFavoriteClickListener {
        fun onRemoveFavoriteClick(favorite: UserFavorite)
    }

    private var onRemoveFavoriteClickListener: OnRemoveFavoriteClickListener? = null

    fun setOnRemoveFavoriteClickListener(listener: OnRemoveFavoriteClickListener) {
        onRemoveFavoriteClickListener = listener
    }

    fun setData(favorites: MutableList<UserFavorite>) {
        this.userFavorites = favorites
        notifyDataSetChanged()
    }

    class UserFavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewStation: TextView = itemView.findViewById(R.id.textViewStation)
        val buttonRemoveFavorite: ImageButton = itemView.findViewById(R.id.buttonDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user_favorite, parent, false)
        return UserFavoriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserFavoriteViewHolder, position: Int) {
        val favorite = userFavorites[position]
        holder.textViewStation.text = favorite.stationName

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(favorite)
        }

        holder.buttonRemoveFavorite.setOnClickListener {
            onRemoveFavoriteClickListener?.onRemoveFavoriteClick(favorite)
        }
    }

    override fun getItemCount(): Int {
        return userFavorites.size
    }

    fun removeFavorite(favorite: UserFavorite) {
        val index = userFavorites.indexOf(favorite)
        if (index != -1) {
            userFavorites.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
