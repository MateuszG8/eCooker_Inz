package com.example.ecooker.ui.editProfile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecooker.MainActivity
import com.example.ecooker.R
import com.example.ecooker.UserViewModel
import com.example.ecooker.databinding.FragmentEditProfileBinding
import com.example.ecooker.models.EditUser
import com.example.ecooker.models.UserInfo
import com.example.ecooker.ui.addRecipe.AddRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private val viewModel: EditProfileViewModel by viewModels()
    private val userViewModel by activityViewModels<UserViewModel>()
    private lateinit var binding: FragmentEditProfileBinding
    private var selectedImageUri: Uri? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var postResultObserver: Observer<Int>
    private var imageChanged : Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    selectedImageUri = it
                    binding.imageView.setImageURI(selectedImageUri)
                    imageChanged = true
                }
            }
        }

        // Nasłuchiwacz dla przycisku do wyboru zdjęcia
        binding.selectImageButton.setOnClickListener {
            openImageChooser()
        }
        binding.nameET.setText(userViewModel.userData.value?.firstName ?: "")
        binding.surnameET.setText(userViewModel.userData.value?.lastName ?: "")
        binding.loginET.setText(userViewModel.userData.value?.userName ?: "")

        // Nasłuchiwacz dla przycisku "Zapisz"
        binding.saveButton.setOnClickListener {

            if(imageChanged){
                selectedImageUri?.let {
                    uploadImage(it)
                } ?: Toast.makeText(context, "Wybierz zdjęcie przed zapisaniem", Toast.LENGTH_SHORT).show()
            }
            
            val firstName = binding.nameET.text.toString()
            val lastName = binding.surnameET.text.toString()
            val userName = binding.loginET.text.toString()
            
            viewModel.editProfile(EditUser(firstName,lastName,userName))

        }
       postResultObserver = Observer { result ->
            when (result) {
                1 -> {
                    Toast.makeText(context, "Zmiany zapisano", Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).refreshNavbar()
                    viewModel.resetLiveData()
                }
                2 -> {
                    Toast.makeText(context, "Zdjecie odrzucone", Toast.LENGTH_SHORT).show()
                    viewModel.resetLiveData()
                }
                3 -> {
                    Toast.makeText(context, "serwer nie odpowiada", Toast.LENGTH_SHORT).show()
                    viewModel.resetLiveData()

                }
            }
        }
        viewModel.postResult.observe(viewLifecycleOwner,postResultObserver)
        val userInfo = userViewModel.userData.value!!
        val correctedUrl = userInfo.image.replace("localhost", "10.0.2.2")
        Glide.with(this)
            .load(correctedUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.mipmap.ic_launcher_round)
            .into(binding.imageView)
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun uploadImage(uri: Uri) {
        val requestFile = getRequestBodyFromUri(uri)


        if (requestFile != null) {
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", "filename.jpg", requestFile)
            viewModel.uploadPhoto(body)
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
}