package com.example.guidemetro.presentation.fragment

import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guidemetro.R
import com.example.guidemetro.UserReview
import com.example.guidemetro.databinding.FragmentUserReviewsBinding
import com.example.guidemetro.presentation.adapter.UserReviewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class FragmentUserReviews : Fragment() {
    private var _binding: FragmentUserReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var userReviewAdapter: UserReviewAdapter
    private lateinit var userId: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        databaseReference = FirebaseDatabase.getInstance().getReference("reviews")

        recyclerView = binding.reviewsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userReviewAdapter = UserReviewAdapter(mutableListOf())
        recyclerView.adapter = userReviewAdapter

        binding.iconButtonBack.setOnClickListener {
            replaceFragment(fragment = FragmentAccount())
        }

        userReviewAdapter.setOnDeleteButtonClickListener(object : UserReviewAdapter.OnDeleteButtonClickListener {
            override fun onDeleteButtonClick(review: UserReview) {
                val reviewId = review.reviewId
                val englishStationName = review.stationNameEn
                userReviewAdapter.removeReview(review)
                Handler().postDelayed({
                    val databaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(englishStationName).child(reviewId)
                    databaseReference.removeValue().addOnSuccessListener {
                        Toast.makeText(requireActivity(), "Комментарий удален", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Log.e("FragmentUserReviews", "Failed to delete review: ${e.message}")
                    }
                }, 250)
            }
        })

        loadUserReviews()
    }

    private fun loadUserReviews() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userReviews = mutableListOf<UserReview>() // Создаем локальный список
                for (stationSnapshot in dataSnapshot.children) {
                    for (reviewSnapshot in stationSnapshot.children) {
                        val reviewId = reviewSnapshot.key
                        val review = reviewSnapshot.getValue(UserReview::class.java)?.copy(reviewId = reviewId ?: "") // Добавляем reviewId к объекту UserReview
                        if (review?.userId == userId) {
                            // Определяем текущий язык интерфейса
                            val currentLanguage = Locale.getDefault().language
                            // Выбираем соответствующее название станции в зависимости от текущего языка
                            val stationName = if (currentLanguage == "ru") {
                                review.stationNameRu
                            } else {
                                review.stationNameEn
                            }
                            // Создаем новый объект UserReview с выбранным названием станции
                            val updatedReview = if (currentLanguage == "ru") {
                                review.copy(stationNameRu = stationName)
                            } else {
                                review.copy(stationNameEn = stationName)
                            }
                            userReviews.add(updatedReview)
                        }
                    }
                }
                // Обновляем данные в адаптере после получения всех отзывов
                userReviewAdapter.setData(userReviews)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        }
        databaseReference.addValueEventListener(valueEventListener)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

