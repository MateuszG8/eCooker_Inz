package com.example.ecooker.ui.categories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecooker.databinding.FragmentCategoriesBinding
import com.example.ecooker.adapters.TagsAdapter
import com.example.ecooker.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private lateinit var viewModel: CategoriesViewModel
    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicjalizacja ViewModel
        viewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)

        viewModel.tagCategories.observe(viewLifecycleOwner) { categoriesMap ->
            val tagsList = viewModel.convertToTagsList(categoriesMap)

            val adapter = TagsAdapter(tagsList) { tag ->
                // Logika nawigacji przy kliknięciu na tag
                val action = R.id.action_global_to_searchFragment
                val bundle = bundleOf("query" to tag)
                findNavController().navigate(action, bundle)
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
        }
    }
}