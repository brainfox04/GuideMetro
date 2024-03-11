package com.example.guidemetro.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.example.guidemetro.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth

class FragmentSignUp : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            val email = binding.emailField.editText?.text.toString().trim()
            val password = binding.passwordField.editText?.text.toString().trim()
            val confirmPassword = binding.passwordConfirm.editText?.text.toString().trim()
            registerUser(email, password, confirmPassword)
        }
    }

    private fun registerUser(email: String, password: String, confirmPassword: String) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            // Поля не должны быть пустыми, показывайте сообщение об ошибке
            showMessage("Поля не должны быть пустыми")
        } else if (password != confirmPassword) {
            // Пароли не совпадают, показывайте сообщение об ошибке
            showMessage("Пароли не совпадают")
        } else {
             auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // После успешной регистрации показываем сообщение об успехе
                    showMessage("Регистрация успешно завершена")
                    replaceFragment(FragmentLogIn())
                } else {
                    // Ошибка регистрации, показывайте сообщение об ошибке
                    showMessage("Ошибка регистрации: ${task.exception?.message}")
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
