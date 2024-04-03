package com.example.guidemetro.presentation.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.example.guidemetro.databinding.FragmentStationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class FragmentForFavoriteStation : Fragment() {
    private var _binding: FragmentStationBinding? = null
    private val binding get() = _binding!!

    private var stationName: String? = null
    private var englishStationName: String? = null
    private var isLiked = false // Переменная для отслеживания состояния лайка
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stationName = it.getString(ARG_STATION_NAME)
        }

        englishStationName = getEnglishStationName(stationName)
    }

    private fun getEnglishStationName(stationName: String?): String {
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
        return stationNameMap[stationName] ?: stationName ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Проверяем, что _binding не равен null, прежде чем обращаться к его элементам
        _binding?.apply {
            Header.text = stationName

            loadLikeState()

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                iconButtonFav.visibility = View.VISIBLE
                actionReview.visibility = View.VISIBLE
            } else {
                iconButtonFav.visibility = View.GONE
                actionReview.visibility = View.GONE
            }

            val currentLocale = Locale.getDefault()
            val selectedLanguage = currentLocale.language

            val reference = FirebaseDatabase.getInstance().getReference("station")
            reference.child(englishStationName ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val descriptionSnapshot = dataSnapshot.child("description")
                    val descriptionPath = if (selectedLanguage == "ru") "ru" else "en"
                    val description = descriptionSnapshot.child(descriptionPath).getValue(String::class.java)
                    Description.text = description
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("Firebase", "Error reading data", databaseError.toException())
                }
            })

            actionReview.setOnClickListener {
                stationName?.let { name ->
                    val bottomSheetFragment = BottomSheet.newInstance(name)
                    bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                }
            }

            iconButtonBack.setOnClickListener {
                replaceFragment(FragmentUserFavorite())
            }

            iconButtonRev.setOnClickListener {
                stationName?.let { name ->
                    val fragment = FragmentForFavoriteStationReviews.newInstance(name)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, fragment)
                        .commit()
                }
            }

            iconButtonFav.setOnClickListener {
                isLiked = !isLiked
                updateLikeButton()
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let { id ->
                    val database = FirebaseDatabase.getInstance()
                    val userFavoritesRef = database.getReference("userFavorites").child(id)
                    val station = englishStationName
                    if (!station.isNullOrEmpty()) {
                        if (isLiked) {
                            userFavoritesRef.child(station).setValue(true)
                        } else {
                            userFavoritesRef.child(station).removeValue()
                        }
                    } else {
                        Log.e(TAG, "Unable to determine like state: station name not defined")
                    }
                }
            }
        }
    }

    private fun updateLikeButton() {
        _binding?.let { binding ->
            val drawableRes = if (isLiked) R.drawable.favorite_fill_24px else R.drawable.favorite_24px
            binding.iconButtonFav.setImageResource(drawableRes)
        }
    }

    private fun loadLikeState() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { userId ->
            val database = FirebaseDatabase.getInstance()
            val userFavoritesRef = database.getReference("userFavorites").child(userId)
            val stationName = englishStationName

            if (!stationName.isNullOrEmpty()) {
                // Получаем состояние лайка из базы данных
                userFavoritesRef.child(stationName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Если в базе данных значение true, устанавливаем состояние лайка в true (заполненное состояние)
                            val liked =
                                dataSnapshot.exists() && dataSnapshot.getValue(Boolean::class.java) == true
                            isLiked = liked
                            // Обновляем внешний вид кнопки
                            updateLikeButton()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e(
                                TAG,
                                "Ошибка при чтении данных из базы данных",
                                databaseError.toException()
                            )
                        }
                    })
            } else {
                Log.e(
                    TAG,
                    "Невозможно определить состояние избранного: название станции не определено"
                )
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    companion object {
        const val ARG_STATION_NAME = "station_name"
        fun newInstance(stationName: String) =
            FragmentForFavoriteStation().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATION_NAME, stationName)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}