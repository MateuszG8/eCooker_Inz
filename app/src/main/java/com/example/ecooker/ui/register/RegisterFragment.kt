package com.example.ecooker.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.ecooker.UserViewModel
import com.example.ecooker.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var binding: FragmentRegisterBinding
    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var registerResultObserver: Observer<Pair<Int, String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        registerResultObserver = Observer { result ->
            when (result.first) {
                1 -> {
                    Toast.makeText(context, "Konto zostało stworzone", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(RegisterFragmentDirections.actionNavRegisterToConfirmEmail())
                    userViewModel.defaultResult()
                }
                2 -> {
                    Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                }
            }
        }

        userViewModel.registerResult.observe(viewLifecycleOwner, registerResultObserver)
        setupTextWatchers()
        binding.startBtn.setOnClickListener {
            if (isValid()) {
                // Dane są prawidłowe, możemy przystąpić do rejestracji użytkownika
                val firstName = binding.nameET.text.toString()
                val lastName = binding.surnameET.text.toString()
                val userName = binding.loginET.text.toString()
                val email = binding.emailET.text.toString()
                val password = binding.passET.text.toString()

                userViewModel.register(firstName, lastName, userName, email, password)
            } else {
                // Jeśli dane są nieprawidłowe, wyświetlamy komunikat
                Toast.makeText(context, "Proszę poprawić błędy w formularzu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Usuwam obserwatora, żeby unikać wycieków pamięci i dublowania
        userViewModel.registerResult.removeObserver(registerResultObserver)
    }
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.,/?';:{}])(?=\\S+$).{8,}$"
        val pattern = Pattern.compile(passwordPattern)
        return pattern.matcher(password).matches()
    }
    private fun isOnlyLetters(input: String): Boolean {
        return input.all { it.isLetter() }
    }

    // Funkcja sprawdzająca czy wszystkie pola są poprawne
    private fun isValid(): Boolean {
        val name = binding.nameET.text.toString()
        val surname = binding.surnameET.text.toString()
        val login = binding.loginET.text.toString()
        val email = binding.emailET.text.toString()
        val password = binding.passET.text.toString()


        if (!isInputNotEmpty(name) || !isOnlyLetters(name)) {
            return false
        }
        if(!isInputNotEmpty(login)){
            return false
        }

        if (!isInputNotEmpty(surname) || !isOnlyLetters(surname)) {
            return false
        }

        if (!isEmailValid(email)) {
            return false
        }

        if (!isPasswordValid(password)) {
            return false
        }

        return true
    }

    private fun setupTextWatchers() {
        binding.nameET.addTextChangedListener(createTextWatcher { validateName() })
        binding.surnameET.addTextChangedListener(createTextWatcher { validateSurname() })
        binding.loginET.addTextChangedListener(createTextWatcher { validateLogin() })
        binding.emailET.addTextChangedListener(createTextWatcher { validateEmail() })
        binding.passET.addTextChangedListener(createTextWatcher { validatePassword() })
    }

    private fun createTextWatcher(validationFunction: () -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validationFunction()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nie jest potrzebne dla walidacji
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nie jest potrzebne dla walidacji
            }
        }
    }

    private fun validateName() {
        val name = binding.nameET.text.toString()
        if (!isInputNotEmpty(name) || !isOnlyLetters(name)) {
            binding.nameTI.error = if (name.isEmpty()) "Pole nie może być puste" else "Tylko litery są dozwolone"
        } else {
            binding.nameTI.error = null
        }
    }

    private fun validateSurname() {
        val surname = binding.surnameET.text.toString()
        if (!isInputNotEmpty(surname) || !isOnlyLetters(surname)) {
            binding.surnameTI.error = if (surname.isEmpty()) "Pole nie może być puste" else "Tylko litery są dozwolone"
        } else {
            binding.surnameTI.error = null
        }
    }

    private fun validateLogin() {
        val login = binding.loginET.text.toString()
        if (!isInputNotEmpty(login)) {
            binding.loginTI.error = "Pole nie może być puste"
        } else {
            binding.loginTI.error = null
        }
    }

    private fun validateEmail() {
        val email = binding.emailET.text.toString()
        if (!isEmailValid(email)) {
            binding.emailTI.error = "Niepoprawny format email"
        } else {
            binding.emailTI.error = null
        }
    }

    private fun validatePassword() {
        val password = binding.passET.text.toString()
        if (!isPasswordValid(password)) {
            binding.passTI.error = "Hasło musi zawierać min. 8 znaków, 1 dużą i małą literę, cyfrę oraz znak specjalny"
        } else {
            binding.passTI.error = null
        }
    }



    private fun isInputNotEmpty(input: String): Boolean {
        return input.isNotEmpty()
    }
}