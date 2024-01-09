package com.example.ecooker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.ecooker.databinding.ActivityMainBinding
import com.example.ecooker.models.UserInfo
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        userViewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if(isLoggedIn){
                updateNavHeader(userViewModel.isLoggedIn.value ?: false,   userViewModel.userData.value)
                navController = findNavController(R.id.nav_host_fragment_content_main)
                drawerLayout = binding.drawerLayout
            }


            updateNavHeader(isLoggedIn, userViewModel.userData.value)
            updateNavContent(isLoggedIn)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setSupportActionBar(binding.appBarMain.toolbar)

        setupSearchFunctionality()


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_categories, R.id.nav_fridge, R.id.nav_ranking, R.id.nav_myRecipes, R.id.nav_savedRecipes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.login.setOnClickListener {
            navController.navigate(R.id.nav_login)
            drawerLayout.closeDrawers()
        }

        binding.register.setOnClickListener {
            navController.navigate(R.id.nav_register)
            drawerLayout.closeDrawers()
        }
        binding.logout.setOnClickListener {
            userViewModel.logout()
        }
    }
    fun refreshNavbar(){
        userViewModel.getUserInfo()
        updateNavHeader(true,userViewModel.userData.value)
        Log.d("Photo", "zaktualizowno interfejs")
    }

    private fun updateNavContent(isLoggedIn: Boolean){
        if (isLoggedIn) {
            binding.navView.menu.clear() // Usuń stare elementy menu
            binding.navView.inflateMenu(R.menu.activity_main_logged_drawer) // Dodaj nowe elementy menu
            binding.login.visibility = View.GONE
            binding.register.visibility = View.GONE
            binding.logout.visibility = View.VISIBLE
        } else {
            // Odtwórz stan dla niezalogowanego użytkownika
            binding.navView.menu.clear()
            binding.login.visibility = View.VISIBLE
            binding.register.visibility = View.VISIBLE
            binding.logout.visibility = View.GONE
            binding.navView.inflateMenu(R.menu.activity_main_drawer)
        }
    }

    private fun updateNavHeader(isLoggedIn: Boolean, userInfo: UserInfo?) {
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val usernameTextView: TextView = headerView.findViewById(R.id.usernameTextView)
        val imageView: ImageView = headerView.findViewById(R.id.imageView)

        if (isLoggedIn && userInfo != null) {
            // Zaktualizuj UI dla zalogowanego użytkownika
            usernameTextView.text = "${userInfo.firstName} ${userInfo.lastName}"
            // Używamy Glide do ładowania zdjęcia z serwera i ustawiamy je jako zaokrąglony avatar
            val correctedUrl = userInfo.image.replace("localhost", "10.0.2.2")
            Glide.with(this)
                .load(correctedUrl)
                .circleCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.mipmap.ic_launcher_round)
                .into(imageView)
            imageView.visibility = View.VISIBLE  // Pokaż ikonkę avatara
            usernameTextView.textSize = 16f // Domyślny rozmiar czcionki
            Log.d("nav",correctedUrl)
            usernameTextView.setOnClickListener {
                navigateToProfileFragment()
            }
        } else {
            // Zaktualizuj UI dla niezalogowanego użytkownika
            usernameTextView.text = getString(R.string.app_name)  // Możesz tu podać nazwę Twojej aplikacji
            imageView.visibility = View.GONE  // Ukryj ikonkę avatara
            usernameTextView.textSize = 48f // 3-krotnie większy rozmiar czcionki
            usernameTextView.setOnClickListener {

            }
        }
        imageView.setOnClickListener {
            navigateToProfileFragment()
        }


    }


    // Funkcja dodana do obsługi wyszukiwania
    private fun setupSearchFunctionality() {
        val searchEditText = binding.appBarMain.searchEditText
        val searchButton = binding.appBarMain.searchButton


        searchButton.setOnClickListener {
            if (searchEditText.visibility == View.GONE) {
                // Pokaż pole tekstowe i zmień ikonę na 'x'
                searchEditText.visibility = View.VISIBLE
                searchButton.setImageResource(R.drawable.ic_close)

                // Uaktywnij pole tekstowe i pokaż klawiaturę
                searchEditText.requestFocus()
                showKeyboard(searchEditText)

            } else {
                // Ukryj pole tekstowe i zmień ikonę na 'search'
                searchEditText.visibility = View.GONE
                searchButton.setImageResource(R.drawable.ic_search)

                // Ukryj klawiaturę i wyczyść pole tekstowe
                hideKeyboard(searchEditText)
                searchEditText.setText("")
            }
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString().trim()

                if (query.isNotEmpty()) {

                    val bundle = bundleOf("query" to query)
                    findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_global_to_searchFragment, bundle)
                    searchEditText.visibility = View.GONE
                    searchButton.setImageResource(R.drawable.ic_search)

                    // Ukryj klawiaturę i wyczyść pole tekstowe
                    hideKeyboard(searchEditText)
                    searchEditText.setText("")

                } else {
                    Toast.makeText(this, "Proszę wprowadzić frazę do wyszukania.", Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }
    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun navigateToProfileFragment() {
        navController.navigate(R.id.action_global_to_profileFragment)
        drawerLayout.closeDrawers()
    }

}