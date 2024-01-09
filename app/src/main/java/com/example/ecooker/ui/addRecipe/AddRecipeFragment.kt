package com.example.ecooker.ui.addRecipe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecooker.adapters.IngredientActions
import com.example.ecooker.databinding.FragmentAddRecipeBinding
import com.example.ecooker.models.Difficulty
import com.example.ecooker.models.Ingredient
import com.example.ecooker.models.AddRecipe
import com.example.ecooker.adapters.IngredientAdapter
import com.example.ecooker.ui.dialogs.AddIngredientDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


@AndroidEntryPoint
class AddRecipeFragment : Fragment() {


    private val viewModel: AddRecipeViewModel by activityViewModels<AddRecipeViewModel>()
    private lateinit var binding: FragmentAddRecipeBinding
    private lateinit var ingredientAdapter: IngredientAdapter
    private var stepsList: MutableList<EditText> = mutableListOf()
    private var stepCounter = 0
    private lateinit var deleteStepButton: View
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var recipeIdObserver: Observer<String>
    private var imageSelected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deleteStepButton = binding.deleteStepButton

        // Inicjalizacja RecyclerView
        val recyclerView: RecyclerView = binding.ingredientRV
        recyclerView.layoutManager = LinearLayoutManager(context)
        ingredientAdapter = IngredientAdapter(listOf(), object : IngredientActions {
            override fun onEdit(position: Int) {
                val ingredientToEdit = viewModel.ingredients.value?.get(position)
                val editDialogFragment = AddIngredientDialogFragment(ingredientToEdit, position)
                editDialogFragment.show(parentFragmentManager, "EditIngredientDialogFragment")
            }

            override fun onDelete(position: Int) {
                AlertDialog.Builder(context!!)
                    .setTitle("Usuwanie składnika")
                    .setMessage("Czy na pewno chcesz usunąć składnik?")
                    .setPositiveButton("Usuń") { dialog, which ->
                        viewModel.deleteIngredient(position)
                    }
                    .setNegativeButton("Anuluj", null)
                    .show()
            }

        })
        recyclerView.adapter = ingredientAdapter

        // Obserwowanie zmian w LiveData
        viewModel.ingredients.observe(viewLifecycleOwner) { newIngredientList ->
            // Uaktualnienie listy w Adapterze
            Log.d("dodało",viewModel.ingredients.value.toString())
            ingredientAdapter.submitList(newIngredientList)
        }

        // Ustawienie przycisku do otwierania dialogu
        binding.addIngredientButton.setOnClickListener {
            val dialog = AddIngredientDialogFragment()
            dialog.show(parentFragmentManager, "AddIngredientDialogFragment")
        }

        val tagsAutoComplete: AutoCompleteTextView = binding.tagsAutoComplete
        val chipGroup: ChipGroup = binding.chipGroup
        viewModel.tags.observe(viewLifecycleOwner, Observer { listOfTags ->
            val tagsAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, listOfTags)
            tagsAutoComplete.setAdapter(tagsAdapter)
        })
        tagsAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedTag = (tagsAutoComplete.adapter as ArrayAdapter<String>).getItem(position)
                ?: return@setOnItemClickListener

            // Tworzenie chipa z wybraną etykietą
            val chip = Chip(context).apply {
                text = selectedTag
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    chipGroup.removeView(this) // Usuwanie chipa po kliknięciu w ikonę zamykania
                }
            }
            chipGroup.addView(chip)
            tagsAutoComplete.text.clear() // Czyszczenie tekstu po dodaniu chipa
        }
        val difficultySpinner: Spinner = binding.difficultySpinner

        // Pobieranie listy trudności w języku polskim
        val difficulties = Difficulty.toPolishStringList()

        // Tworzenie adaptera dla spinnera
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficulties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = adapter

        // Słuchacz dla spinnera
        difficultySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedDifficultyInPolish = parent.getItemAtPosition(position).toString()
                val selectedDifficultyEnum = Difficulty.values()[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        addStep()
        binding.addStepButton.setOnClickListener {
            addStep()
        }


        deleteStepButton.visibility = View.GONE
        deleteStepButton.setOnClickListener {
            removeLastStep()
        }

        binding.saveButton.setOnClickListener {
            if(validateForm()) {
            // Pobieranie danych z pól tekstowych
            val recipeName = binding.RecipeNameET.text.toString()
            val description = binding.descriptionET.text.toString().ifBlank {
                null
            }
            val calories: Int? = if (binding.caloriesET.text.toString().isBlank()) {
                null
            } else {
                binding.caloriesET.text.toString().toIntOrNull()
            }
            val currentIngredientsList: List<Ingredient> = viewModel.ingredients.value?.toList() ?: emptyList()
            val portions = binding.amountET.text.toString().toInt()
            val instructions = stepsList.map { it.text.toString() }
            val time = binding.timeET.text.toString().toInt()
            // Pobieranie tagów
            val tags = mutableListOf<String>()
            for (i in 0 until binding.chipGroup.childCount) {
                val chip = binding.chipGroup.getChildAt(i) as? Chip
                chip?.let { tags.add(it.text.toString()) }
            }
            val selectedDifficulty = Difficulty.values()[difficultySpinner.selectedItemPosition]
            

                val newRecipe = AddRecipe(
                    name = recipeName,
                    description = description,
                    ingredients = currentIngredientsList,
                    instructions = instructions,
                    tags = tags,
                    difficulty = selectedDifficulty,
                    calories = calories,
                    portions = portions,
                    time = time
                )

                viewModel.addRecipe(newRecipe){
                    findNavController().navigate(AddRecipeFragmentDirections.actionNavAddRecipeToNavHome())
                    Toast.makeText(context,"Przepis dodano",Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.RecipeNameET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                val namePattern = "^[\\p{L} \\s]+$" // Używamy Unicode property \p{L} dla liter, \s dla spacji.
                if (!s.toString().matches(namePattern.toRegex())) {
                    binding.recipeNameIL.error = "Nazwa może zawierać tylko litery i spacje."
                } else {
                    binding.recipeNameIL.error = null
                }
            }
        })
        binding.amountET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { text ->
                    if (text.isNotEmpty()) {
                        val number = text.toIntOrNull()
                        when {
                            number == null || number <= 0 -> {

                                binding.amountTIL.error = "Liczba musi być większa od 0"
                            }
                            number.toDouble() != number.toDouble().toInt().toDouble() -> {

                                binding.amountTIL.error = "Liczba musi być całkowita"
                            }
                            else -> {

                                binding.amountTIL.error = null
                            }
                        }
                    } else {

                        binding.amountTIL.error = "To pole jest wymagane"
                    }
                }
            }
        })
        binding.timeET.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Snackbar.make(v, "To jest czas przygotowania posiłku.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.timeET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { text ->
                    if (text.isNotEmpty()) {
                        val number = text.toIntOrNull()
                        when {
                            number == null || number <= 0 -> {
                                binding.timeTIL.error = "Liczba musi być większa od 0"
                            }
                            number.toDouble() != number.toDouble().toInt().toDouble() -> {
                                binding.timeTIL.error = "Liczba musi być całkowita"
                            }
                            else -> {
                                binding.timeTIL.error = null
                            }
                        }
                    } else {
                        binding.timeTIL.error = "To pole jest wymagane"
                    }
                }
            }
        })
        binding.caloriesET.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Snackbar.make(v, "To pole jest opcjonalne.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.caloriesET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                s?.toString()?.let { text ->
                    if (text.isNotEmpty()) {
                        val number = text.toIntOrNull()
                        when {
                            number == null || number <= 0 -> {

                                binding.caloriesTIL.error = "Liczba musi być większa od 0"
                            }
                            number.toDouble() != number.toDouble().toInt().toDouble() -> {

                                binding.caloriesTIL.error = "Liczba musi być całkowita"
                            }
                            else -> {

                                binding.caloriesTIL.error = null
                            }
                        }
                    } else {

                        binding.caloriesTIL.error = null
                    }
                }
            }
        })

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    selectedImageUri = it
                    binding.RecipeIV.setImageURI(selectedImageUri)
                    imageSelected = true
                }
            }
        }
        recipeIdObserver = Observer { id ->
            if (imageSelected)
                selectedImageUri?.let {
                    Log.d("ID", id)
                    uploadImage(id,it)
                } ?: Toast.makeText(context, "Wybierz zdjęcie przed zapisaniem", Toast.LENGTH_SHORT).show()
        }
        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }
        viewModel.receivedRecipeId.observe(viewLifecycleOwner, recipeIdObserver)
        
    }

    private fun validateForm(): Boolean {
        if(binding.RecipeNameET.text.isNullOrEmpty()) {
            binding.recipeNameIL.error = "Pole nazwy przepisu jest wymagane."
            return false
        } else {
            binding.recipeNameIL.error = null
        }

        // Validate ingredients
        if(binding.ingredientRV.adapter?.itemCount == 0) {
            Toast.makeText(context, "Proszę dodać przynajmniej jeden składnik.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate instructions
        var allStepsValid = true
        for (editText in stepsList) {
            if (editText.text.toString().trim().isEmpty()) {
                editText.error = "Ten krok nie może być pusty."
                allStepsValid = false
            } else {
                editText.error = null
            }
        }
        if (!allStepsValid) {
            Toast.makeText(context, "Wszystkie kroki muszą być opisane.", Toast.LENGTH_SHORT).show()
            return false
        }
        // Validate tags
        if (binding.chipGroup.childCount == 0) {
            Toast.makeText(context, "Proszę wybrać przynajmniej jeden tag.", Toast.LENGTH_SHORT).show()
            return false
        }
        // Walidacja czasu przygotowania
        binding.timeET.text?.toString()?.let {
            if (it.isEmpty() || it.toIntOrNull() == null || it.toInt() <= 0) {
                binding.timeTIL.error = "Podaj poprawny czas (większy od 0)."
                return false
            } else if (it.toDouble() != it.toInt().toDouble()) {
                binding.timeTIL.error = "Czas musi być liczbą całkowitą."
                return false
            } else {
                binding.timeTIL.error = null
            }
        }

        // Walidacja ilości porcji
        binding.amountET.text?.toString()?.let {
            if (it.isEmpty() || it.toIntOrNull() == null || it.toInt() <= 0) {
                binding.amountTIL.error = "Podaj poprawną ilość porcji (większą od 0)."
                return false
            } else if (it.toDouble() != it.toInt().toDouble()) {
                binding.amountTIL.error = "Ilość porcji musi być liczbą całkowitą."
                return false
            } else {
                binding.amountTIL.error = null // Usuwa błąd, jeśli wszystko jest w porządku
            }
        }

        return true
    }


    private fun addStep() {
        val (stepView, editText) = createStepView(requireContext(), stepCounter+1)
        binding.instructionStepsLayout.addView(stepView)

        stepsList.add(editText)

        stepCounter++

        updateDeleteButtonVisibility()
    }
    private fun updateDeleteButtonVisibility() {
        deleteStepButton.visibility = if (stepCounter >= 2) View.VISIBLE else View.GONE
    }

    private fun createStepView(context: Context, stepNumber: Int): Pair<TextInputLayout, TextInputEditText> {
        val inputLayout = TextInputLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            hint = "Krok $stepNumber"
        }

        // Tworzenie TextInputEditText
        val editText = TextInputEditText(inputLayout.context).apply {
            id = View.generateViewId() // Generowanie unikalnego ID dla tego widoku
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            gravity = Gravity.TOP or Gravity.LEFT
            minLines = 4
            overScrollMode = View.OVER_SCROLL_ALWAYS
            setPadding(16, 16, 16, 16)
            isScrollContainer = true
            setHorizontallyScrolling(false)
        }

        inputLayout.addView(editText)

        return Pair(inputLayout, editText)
    }

    private fun removeLastStep() {
        if (stepsList.isNotEmpty()) {
            if (stepCounter > 0) {
                Log.d("removeLastStep", "Number of children before removal: ${binding.instructionStepsLayout.childCount}")
                Log.d("removeLastStep", "Step counter: $stepCounter")
                binding.instructionStepsLayout.removeViewAt(stepCounter - 1)
                stepsList.removeAt(stepsList.size - 1)

                stepCounter--

                updateDeleteButtonVisibility()
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun uploadImage(id: String, uri: Uri) {
        val requestFile = getRequestBodyFromUri(uri)


        if (requestFile != null) {
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", "filename.jpg", requestFile)
            viewModel.uploadPhoto(id,body)
        } else {
            Toast.makeText(context, "Błąd podczas przetwarzania zdjęcia", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getRequestBodyFromUri(uri: Uri): RequestBody? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        inputStream?.close()
        return byteArray?.toRequestBody(requireContext().contentResolver.getType(uri)?.toMediaTypeOrNull())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearIngredientsList()
    }

}