package com.example.guidemetro.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.guidemetro.databinding.BottomSheetReviewsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class BottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stationName = arguments?.getString(ARG_STATION_NAME)
        binding.stationName.text = stationName

        val stationNameMapEn = mapOf(
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

        val stationNameMapRu = mapOf(
            "Kurskaya" to "Курская",
            "Taganskaya" to "Таганская",
            "Paveletskaya" to "Павелецкая",
            "Dobryninskaya" to "Добрынинская",
            "Oktyabrskaya" to "Октябрьская",
            "Park Kultury" to "Парк культуры",
            "Kiyevskaya" to "Киевская",
            "Krasnopresnenskaya" to "Краснопресненская",
            "Belorusskaya" to "Белорусская",
            "Novoslobodskaya" to "Новослободская",
            "Prospekt Mira" to "Проспект Мира",
            "Komsomolskaya" to "Комсомольская"
        )

        val russianStationName = stationNameMapRu[stationName] ?: stationName ?: ""

        val englishStationName = stationNameMapEn[stationName] ?: stationName ?: ""

        binding.reviewButton.setOnClickListener {
            val reviewText = binding.reviewField.editText?.text.toString().trim()
            val rating = binding.ratingBar.rating.toInt() // Получаем оценку из RatingBar

            if (reviewText.isEmpty()) {
                showMessage("Пожалуйста, введите отзыв")
                return@setOnClickListener
            }

            if (rating == 0) {
                showMessage("Пожалуйста, поставьте оценку")
                return@setOnClickListener
            }

            if (!isInternetAvailable(requireContext())) {
                showMessage("Отсутствует подключение к интернету. Отзыв будет отправлен при восстановлении связи.")
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (stationName != null && userId != null) {
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("reviews").child(englishStationName)
                val reviewId = reference.push().key // Генерация уникального идентификатора для отзыва
                val reviewData = HashMap<String, Any>()
                reviewData["stationNameRu"] = russianStationName
                reviewData["stationNameEn"] = englishStationName
                reviewData["text"] = reviewText
                reviewData["userId"] = userId
                reviewData["rating"] = rating
                reference.child(reviewId!!).setValue(reviewData)
                    .addOnSuccessListener {
                        showMessage("Отзыв добавлен")
                        binding.reviewField.editText?.text?.clear() // Очистка поля ввода
                        binding.ratingBar.rating = 0f // Сброс оценки после сохранения
                        hideKeyboard() // Скрыть клавиатуру
                        dismiss() // Закрыть BottomSheet после успешного добавления отзыва
                    }
                    .addOnFailureListener { e ->
                        // Обработка ошибок
                }
            }
        }

    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.reviewField.editText?.windowToken, 0)
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val ARG_STATION_NAME = "station_name"

        fun newInstance(stationName: String) =
            BottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_STATION_NAME, stationName)
                }
            }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo?.isConnected ?: false
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
