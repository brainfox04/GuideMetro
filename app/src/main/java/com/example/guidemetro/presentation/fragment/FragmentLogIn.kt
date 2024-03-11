package com.example.guidemetro.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.guidemetro.R
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.example.guidemetro.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class FragmentLogIn : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.logInButton.setOnClickListener {
            loginUser()
        }

        binding.signInButton.setOnClickListener {
            replaceFragment(fragment = FragmentSignUp())
        }

    }

    private fun loginUser() {
        val email = binding.loginField.editText?.text.toString().trim()
        val password = binding.passwordField.editText?.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    replaceFragment(fragment = FragmentAccount())
                } else {
                    // Ошибка входа, показ сообщения об ошибке
                    when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            showMessage("Неверный логин или пароль. Пожалуйста, попробуйте снова.")
                        }
                        else -> {
                            showMessage("Ошибка входа: ${exception?.message}")
                        }
                    }
                }
            }
        } else {
            // Если email или пароль не введены, показ сообщения об ошибке
            showMessage("Email и пароль не должны быть пустыми")
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