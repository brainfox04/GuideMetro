package com.example.guidemetro.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guidemetro.R
import com.example.guidemetro.Review

class ReviewAdapter(private var reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    fun setData(reviews: List<Review>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val reviewTextView: TextView = itemView.findViewById(R.id.textViewReview)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)

        // Метод для связывания данных с элементами макета
        fun bind(review: Review) {
            reviewTextView.text = review.text
            ratingBar.rating = review.rating
        }
    }

    // Создаем ViewHolder при создании нового экземпляра
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(itemView)
    }

    // Привязываем данные к ViewHolder при отображении элемента списка
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    // Возвращает количество элементов в списке
    override fun getItemCount(): Int {
        return reviews.size
    }
}
