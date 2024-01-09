package com.example.ecooker.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.ecooker.R
import com.example.ecooker.models.Ingredient
import com.example.ecooker.ui.addRecipe.AddRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIngredientDialogFragment(private val ingredientToEdit: Ingredient? = null, private val position: Int? = null) : DialogFragment() {

    private val viewModel: AddRecipeViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.ingredient_dialog, null)

        val ingredientName: EditText = view.findViewById(R.id.ingredientName)
        val ingredientAmount: EditText = view.findViewById(R.id.ingredientAmount)
        val ingredientUnit: Spinner = view.findViewById(R.id.ingredientUnit)

        val units = arrayOf("g", "kg", "ml", "l", "szt.","łyżka", "łyżeczka", "szklanka" )
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, units)
        ingredientUnit.adapter = unitAdapter

        // Pre-fill with existing ingredient data if editing
        ingredientToEdit?.let {
            ingredientName.setText(it.name)
            ingredientAmount.setText(it.ammount.toString())
            val unitPosition = units.indexOf(it.unit)
            ingredientUnit.setSelection(if (unitPosition != -1) unitPosition else 0)
        }

        // Configure the dialog with either "Add" or "Update" positive button
        val dialogBuilder = AlertDialog.Builder(activity)
            .setView(view)
            .setPositiveButton(ingredientToEdit?.let { "Zapisz" } ?: "Dodaj") { _, _ ->
                val name = ingredientName.text.toString()
                val amount = ingredientAmount.text.toString().toIntOrNull() ?: 0
                val unit = ingredientUnit.selectedItem.toString()
                val newIngredient = Ingredient(name, amount, unit)

                if (ingredientToEdit != null && position != null) {
                    // Update ingredient
                    viewModel.editIngredient(position, newIngredient)
                } else {
                    // Add new ingredient
                    viewModel.addIngredient(newIngredient)
                }
            }
            .setNeutralButton(ingredientToEdit?.let { "" } ?: "Dodaj kolejny") { _, _ ->
                val newIngredient = Ingredient(ingredientName.text.toString(), ingredientAmount.text.toString().toInt(), ingredientUnit.selectedItem.toString())
                viewModel.addIngredient(newIngredient)
                val dialog = AddIngredientDialogFragment()
                dialog.show(parentFragmentManager, "AddIngredientDialogFragment")
            }
            .setNegativeButton("Anuluj", null)

        return dialogBuilder.create()
    }
}
