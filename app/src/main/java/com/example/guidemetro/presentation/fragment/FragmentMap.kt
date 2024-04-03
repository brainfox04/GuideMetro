package com.example.guidemetro.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.example.guidemetro.presentation.fragment.StationFragment
import com.example.guidemetro.databinding.FragmentMapBinding
import java.util.Locale

class FragmentMap : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionFind.setOnClickListener {
            val stationName = binding.findField.editText?.text.toString().trim()

            val currentLanguage = Locale.getDefault().language

            val stationNameMap = if (currentLanguage == "ru") {
                mapOf(
                    "Курская" to "Курская",
                    "Таганская" to "Таганская",
                    "Павелецкая" to "Павелецкая",
                    "Добрынинская" to "Добрынинская",
                    "Октябрьская" to "Октябрьская",
                    "Парк культуры" to "Парк культуры",
                    "Киевская" to "Киевская",
                    "Краснопресненская" to "Краснопресненская",
                    "Белорусская" to "Белорусская",
                    "Новослободская" to "Новослободская",
                    "Проспект Мира" to "Проспект Мира",
                    "Комсомольская" to "Комсомольская"
                )
            } else {
                mapOf(
                    "Kurskaya" to "Kurskaya",
                    "Taganskaya" to "Taganskaya",
                    "Paveletskaya" to "Paveletskaya",
                    "Dobryninskaya" to "Dobryninskaya",
                    "Oktyabrskaya" to "Oktyabrskaya",
                    "Park Kultury" to "Park Kultury",
                    "Kiyevskaya" to "Kiyevskaya",
                    "Krasnopresnenskaya" to "Krasnopresnenskaya",
                    "Belorusskaya" to "Belorusskaya",
                    "Novoslobodskaya" to "Novoslobodskaya",
                    "Prospekt Mira" to "Prospekt Mira",
                    "Komsomolskaya" to "Komsomolskaya"
                )
            }

            if (stationName.isNotEmpty() && stationNameMap.containsKey(stationName)) {
                val translatedStationName = stationNameMap[stationName] ?: ""
                // Открываем фрагмент с названием станции
                val fragment = StationFragment.newInstance(translatedStationName)
                val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            } else {
                // Если название станции не найдено, отобразить сообщение об ошибке
                showMessage("Станция не найдена")
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}