package com.example.ecooker.ui.confirmEmail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ecooker.R
import com.example.ecooker.databinding.FragmentConfirmEmailBinding
import com.example.ecooker.databinding.FragmentHomeBinding


class ConfirmEmailFragment : Fragment() {

    private lateinit var binding : FragmentConfirmEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goToLoginButton.setOnClickListener {
            findNavController().navigate(ConfirmEmailFragmentDirections.actionNavConfirmEmailToLogin())
        }


    }
}