package com.example.guidemetro.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guidemetro.R
import com.example.guidemetro.Review
import com.example.guidemetro.databinding.FragmentReviewsBinding
import com.example.guidemetro.presentation.adapter.ReviewAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentReviews : Fragment() {
    private var _binding: FragmentReviewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем stationName из аргументов
        val stationName = arguments?.getString("stationName")
        // Устанавливаем stationName в TextView
        binding.stationName.text = stationName
        val stationNameMap = mapOf(
            "Курская" to "Kurskaya",
            "Таганская" to "Taganskaya",
            "Павелецкая" to "Paveletskaya",
            "Добрынинская" to "Dobryninskaya",
            "Октябрьская" to "Oktyabrskaya",
            "Парк культуры" to "Park Kultury",
            "Киевская" to "Kiyevskaya",
            "Краснопресненская" to "Krasnopresnenskaya",
            "Белорусская" to "Belorusskaya",
            "Новослободская" to "Novoslobodskaya",
            "Проспект Мира" to "Prospekt Mira",
            "Комсомольская" to "Komsomolskaya"
        )

        val englishStationName = stationNameMap[stationName] ?: stationName ?: ""
        // Initialize recyclerView
        recyclerView = binding.reviewsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reviewAdapter = ReviewAdapter(emptyList())
        recyclerView.adapter = reviewAdapter

        // Load reviews from Firebase for the specified station and pass them to the adapter
        loadReviewsForStation(englishStationName ?: "")

        binding.iconButtonBack.setOnClickListener {
            // Возвращаемся на предыдущий фрагмент (фрагмент станции)
            parentFragmentManager.popBackStack()

            // Создаем новый экземпляр фрагмента станции с передачей имени станции
            val fragment = StationFragment.newInstance(stationName!!)
            replaceFragment(fragment)
        }
    }
    private fun loadReviewsForStation(englishStationName: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(englishStationName)

        // Слушатель для получения данных из Firebase
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reviews = mutableListOf<Review>()
                for (snapshot in dataSnapshot.children) {
                    // Получаем текст отзыва и оценку из снимка (snapshot)
                    val reviewText = snapshot.child("text").getValue(String::class.java)
                    val rating = snapshot.child("rating").getValue(Float::class.java)

                    reviewText?.let { text ->
                        rating?.let { rating ->
                            val review = Review(text, rating)
                            reviews.add(review)
                        }
                    }
                }
                // Обновляем данные в адаптере
                reviewAdapter.setData(reviews)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок при получении данных из Firebase
            }
        }
        // Добавляем слушатель к базе данных Firebase
        databaseReference.addValueEventListener(valueEventListener)
    }


    companion object {
        fun newInstance(stationName: String): FragmentReviews {
            val fragment = FragmentReviews()
            val args = Bundle().apply {
                putString("stationName", stationName)
            }
            fragment.arguments = args
            return fragment
        }
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
