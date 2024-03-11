package com.example.guidemetro.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.google.firebase.auth.FirebaseAuth
import com.example.guidemetro.databinding.ActivityMainBinding
import com.example.guidemetro.presentation.fragment.FragmentAccount
import com.example.guidemetro.presentation.fragment.FragmentLogIn
import com.example.guidemetro.presentation.fragment.FragmentMap
import com.example.guidemetro.presentation.fragment.FragmentNews

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpBottomNavigationView()
        replaceFragment(fragment = FragmentMap())

        auth = FirebaseAuth.getInstance()
    }


    private fun setUpBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.map -> {
                    replaceFragment(fragment = FragmentMap())
                    true
                }

                R.id.news -> {
                    replaceFragment(fragment = FragmentNews())
                    true
                }

                R.id.account -> {
                    // Проверяем, авторизован ли пользователь
                    if (auth.currentUser != null) {
                        // Пользователь авторизован, открываем фрагмент аккаунта
                        replaceFragment(fragment = FragmentAccount())
                    } else {
                        // Пользователь не авторизован, открываем фрагмент входа
                        replaceFragment(fragment = FragmentLogIn())
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }
}