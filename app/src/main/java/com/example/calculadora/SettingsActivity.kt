package com.example.calculadora

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

import com.example.calculadora.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var currentTheme: Int = 0
    private var newTheme: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = sharedPreferences.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        newTheme = currentTheme

        setupToolbar()
        setupThemeSection()
        setupDecimalPlacesSection()
        setupBackPressedHandler() // Novo método
        loadCurrentSettings()
    }

    private fun setupBackPressedHandler() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Aplica o tema apenas se houve mudança
                if (newTheme != currentTheme) {
                    applyThemeChange()
                }
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Configurações"

        binding.toolbar.setNavigationOnClickListener {
            // Aplica o tema apenas se houve mudança
            if (newTheme != currentTheme) {
                applyThemeChange()
            }
            finish()
        }
    }

    private fun setupThemeSection() {
        binding.cardTheme.setOnClickListener {
            // Cicla entre os temas: Claro → Escuro → Sistema → Claro
            newTheme = when (newTheme) {
                AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_YES
                AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
            updateThemeUI(newTheme)
        }
    }

    private fun setupDecimalPlacesSection() {
        binding.cardDecimal2.setOnClickListener { setDecimalPlaces(2) }
        binding.cardDecimal3.setOnClickListener { setDecimalPlaces(3) }
        binding.cardDecimal4.setOnClickListener { setDecimalPlaces(4) }
        binding.cardDecimal5.setOnClickListener { setDecimalPlaces(5) }
        binding.cardDecimal6.setOnClickListener { setDecimalPlaces(6) }
    }

    private fun applyThemeChange() {
        sharedPreferences.edit().putInt("app_theme", newTheme).apply()
        AppCompatDelegate.setDefaultNightMode(newTheme)
    }

    private fun setDecimalPlaces(places: Int) {
        sharedPreferences.edit().putInt("decimal_places", places).apply()
        updateDecimalPlacesUI(places)
    }

    private fun loadCurrentSettings() {
        val decimalPlaces = sharedPreferences.getInt("decimal_places", 2)
        updateThemeUI(currentTheme)
        updateDecimalPlacesUI(decimalPlaces)
    }

    private fun updateThemeUI(theme: Int) {
        val themeText = when (theme) {
            AppCompatDelegate.MODE_NIGHT_NO -> "Tema Claro"
            AppCompatDelegate.MODE_NIGHT_YES -> "Tema Escuro"
            else -> "Tema do Sistema"
        }
        binding.themeValue.text = themeText
    }

    private fun updateDecimalPlacesUI(places: Int) {
        listOf(
            binding.cardDecimal2,
            binding.cardDecimal3,
            binding.cardDecimal4,
            binding.cardDecimal5,
            binding.cardDecimal6
        ).forEach { card ->
            card.strokeWidth = 0
        }

        when (places) {
            2 -> binding.cardDecimal2.strokeWidth = 3
            3 -> binding.cardDecimal3.strokeWidth = 3
            4 -> binding.cardDecimal4.strokeWidth = 3
            5 -> binding.cardDecimal5.strokeWidth = 3
            6 -> binding.cardDecimal6.strokeWidth = 3
        }
    }

    companion object {
        fun getDecimalPlaces(prefs: SharedPreferences): Int {
            return prefs.getInt("decimal_places", 2)
        }

        fun getCurrentTheme(prefs: SharedPreferences): Int {
            return prefs.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}