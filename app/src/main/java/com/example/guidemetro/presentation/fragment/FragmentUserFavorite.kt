package com.example.guidemetro.presentation.fragment

import android.os.Handler
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
import com.example.guidemetro.UserFavorite
import com.example.guidemetro.databinding.FragmentUserFavoriteBinding
import com.example.guidemetro.presentation.adapter.UserFavoriteAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Locale

class FragmentUserFavorite : Fragment() {
    private var _binding: FragmentUserFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var userFavoriteAdapter: UserFavoriteAdapter
    private lateinit var userId: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Указываем путь к базе данных, где хранятся избранные пользовательские объекты
        databaseReference = FirebaseDatabase.getInstance().getReference("userFavorites").child(userId)

        recyclerView = binding.reviewsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userFavoriteAdapter = UserFavoriteAdapter(mutableListOf())
        recyclerView.adapter = userFavoriteAdapter

        binding.iconButtonBack.setOnClickListener {
            replaceFragment(fragment = FragmentAccount())
        }

        userFavoriteAdapter.setOnRemoveFavoriteClickListener(object : UserFavoriteAdapter.OnRemoveFavoriteClickListener {
            override fun onRemoveFavoriteClick(favorite: UserFavorite) {
                val stationName = favorite.stationName // Исходное название станции
                // Удаление из RecyclerView
                userFavoriteAdapter.removeFavorite(favorite)
                // Удаление из базы данных
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                // Получить английское название станции
                val englishStationName = getEnglishStationName(stationName)
                Handler().postDelayed({
                    val databaseReference = FirebaseDatabase.getInstance().getReference("userFavorites").child(userId).child(englishStationName!!)
                    databaseReference.removeValue().addOnSuccessListener {
                        Toast.makeText(requireActivity(), "Станция удалена из избранного", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Log.e("FragmentUserFavorite", "Failed to remove favorite: ${e.message}")
                    }
                }, 250)
            }
        })

        userFavoriteAdapter.setOnItemClickListener(object : UserFavoriteAdapter.OnItemClickListener {
            override fun onItemClick(favorite: UserFavorite) {
                val stationName = favorite.stationName
                val fragment = FragmentForFavoriteStation.newInstance(stationName)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        })
        loadUserFavorites()
    }

    private fun getEnglishStationName(stationName: String?): String? {

        return when (stationName) {
            "Курская" -> "Kurskaya"
            "Таганская" -> "Taganskaya"
            "Павелецкая" -> "Paveletskaya"
            "Добрынинская" -> "Dobryninskaya"
            "Октябрьская" ->"Oktyabrskaya"
            "Парк культуры" -> "Park Kultury"
            "Киевская" -> "Kiyevskaya"
            "Краснопресненская" -> "Krasnopresnenskaya"
            "Белорусская" -> "Belorusskaya"
            "Новослободская" -> "Novoslobodskaya"
            "Проспект Мира" -> "Prospekt Mira"
            "Комсомольская" -> "Komsomolskaya"
            else -> stationName
        }
    }

    private fun loadUserFavorites() {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userFavorites = mutableListOf<UserFavorite>() // Создаем локальный список избранных объектов
                for (favoriteSnapshot in dataSnapshot.children) {
                    // Получаем данные из снимка и добавляем их в список избранных объектов пользователя
                    val stationName = favoriteSnapshot.key
                    val isFavorite = favoriteSnapshot.getValue(Boolean::class.java) ?: false
                    val localizedStationName = if (isRussianLanguage()) getLocalizedStationName(stationName) else stationName
                    val userFavorite = UserFavorite(localizedStationName ?: "", isFavorite)
                    userFavorites.add(userFavorite)
                }
                // Обновляем данные в адаптере после получения всех избранных объектов пользователя
                userFavoriteAdapter.setData(userFavorites)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        // Добавляем слушатель событий значения к базе данных, чтобы отслеживать изменения в избранных объектах пользователя
        databaseReference.addValueEventListener(valueEventListener)
    }

    private fun isRussianLanguage(): Boolean {
        val currentLanguage = Locale.getDefault().language
        return currentLanguage == "ru"
    }


    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    private fun getLocalizedStationName(stationName: String?): String? {
        // Проверяем, прикреплен ли фрагмент к активности
        if (!isAdded) {
            return stationName // Возвращаем исходное название, если фрагмент не прикреплен к активности
        }
        // Получаем идентификатор ресурса для локализованного названия станции
        val resourceId = requireActivity().resources.getIdentifier(stationName?.toLowerCase()?.replace(" ", ""), "string", requireActivity().packageName)
        // Если идентификатор ресурса найден, возвращаем локализованное название станции, иначе возвращаем исходное название
        return if (resourceId != 0) {
            requireActivity().resources.getString(resourceId)
        } else {
            stationName
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}