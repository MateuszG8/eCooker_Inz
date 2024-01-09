package com.example.ecooker.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ecooker.R
import com.example.ecooker.UserViewModel
import com.example.ecooker.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var loginResultObserver: Observer<Pair<Int, String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loginResultObserver = Observer { result ->
            when (result.first) {
                1 -> {
                    Toast.makeText(context, "Zalogowano pomyślnie", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_nav_login_to_nav_home)
                }
                2 -> {
                    Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    Toast.makeText(context, result.second, Toast.LENGTH_SHORT).show()
                }
            }
        }

        userViewModel.loginResult.observe(viewLifecycleOwner, loginResultObserver)

        binding.startBtn.setOnClickListener {
            val userName = binding.loginET.text.toString()
            val password = binding.passET.text.toString()
            userViewModel.login(userName, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Usuwam obserwatora, żeby unikać wycieków pamięci i dublowania
        userViewModel.loginResult.removeObserver(loginResultObserver)
    }
}