package com.example.ecooker.ui.fridge

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.ecooker.R
import com.example.ecooker.databinding.FragmentFridgeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FridgeFragment : Fragment() {


    private lateinit var binding: FragmentFridgeBinding
    private lateinit var viewModel: FridgeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFridgeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.searchButton.setOnClickListener {
            val query = binding.ingredientsET.text.toString()
            val bundle = Bundle()
            bundle.putString("queryFridge", query)
            findNavController().navigate(R.id.action_global_to_searchFragment, bundle)
        }


    }
}