package com.example.guidemetro.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import kotlin.collections.MutableList
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guidemetro.R
import com.example.guidemetro.UserReview
import java.util.Locale

class UserReviewAdapter(private var userReviews: MutableList<UserReview>) : RecyclerView.Adapter<UserReviewAdapter.UserReviewViewHolder>() {


    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClick(review: UserReview)
    }


    private var onDeleteButtonClickListener: OnDeleteButtonClickListener? = null

    fun setOnDeleteButtonClickListener(listener: OnDeleteButtonClickListener) {
        onDeleteButtonClickListener = listener
    }

    fun setData(reviews: MutableList<UserReview>) {
        this.userReviews = reviews
        notifyDataSetChanged()
    }

    class UserReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewStation: TextView = itemView.findViewById(R.id.textViewStation)
        val textViewReview: TextView = itemView.findViewById(R.id.textViewReview)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user_review, parent, false)
        return UserReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserReviewViewHolder, position: Int) {
        val review = userReviews[position]

        val currentLanguage = Locale.getDefault().language
        val stationName = if (currentLanguage == "ru") {
            review.stationNameRu
        } else {
            review.stationNameEn
        }

        holder.textViewStation.text = stationName
        holder.textViewReview.text = review.text
        holder.ratingBar.rating = review.rating

        holder.buttonDelete.setOnClickListener {
            onDeleteButtonClickListener?.onDeleteButtonClick(review)
        }
    }

    override fun getItemCount(): Int {
        return userReviews.size
    }

    fun removeReview(review: UserReview) {
        val index = userReviews.indexOf(review)
        if (index != -1) {
            userReviews.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
