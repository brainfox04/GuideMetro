package com.example.guidemetro.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.example.guidemetro.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class FragmentAccount : Fragment(){
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        // Устанавливаем e-mail пользователя в TextView
        binding.userEmail.text = currentUser?.email ?: "Неизвестный пользователь"

        binding.reviewButton.setOnClickListener {
            replaceFragment(fragment = FragmentUserReviews())
        }

        binding.favoriteButton.setOnClickListener {
            replaceFragment(fragment = FragmentUserFavorite())
        }

        binding.signOutButton.setOnClickListener {
            // Выполнение выхода из учетной записи пользователя
            auth.signOut()

            // Показываем сообщение об успешном выходе
            showMessage("Вы успешно вышли из учетной записи")

            // Замена текущего фрагмента на фрагмент входа
            replaceFragment(fragment = FragmentLogIn())
        }

        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            val newLang = if (isChecked) "en" else "ru"
            setLocale(newLang)
            updateTexts()

        }

        val sharedPref = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isSwitchChecked = sharedPref.getBoolean("isEnglishSelected", false)
        binding.switchLanguage.isChecked = isSwitchChecked
        val appLanguage = sharedPref.getString("AppLanguage", "ru") ?: "ru"
        setLocale(appLanguage)
        updateTexts()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Сохраняем выбранный язык и состояние переключателя
        val sharedPref = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("AppLanguage", languageCode)
            putBoolean("isEnglishSelected", languageCode == "en")
            apply()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun updateTexts() {
        binding.reviewButton.text = getString(R.string.myReview)
        binding.favoriteButton.text = getString(R.string.myFavorite)
        binding.newsHeader.text = getString(R.string.account)
        binding.Email.text = getString(R.string.emailAccount)
        binding.signOutButton.text = getString(R.string.signOut)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
