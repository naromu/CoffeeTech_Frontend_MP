package com.example.coffetech.viewmodel.Plot

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri
/**
 * ViewModel responsible for managing the state and logic of creating plot information,
 * including handling plot name and coffee variety selection.
 */
class CreatePlotInformationViewModel(
    private val savedStateHandle: SavedStateHandle // Agregamos SavedStateHandle
) : ViewModel() {

    // Usamos SavedStateHandle para mantener el estado
    private val _plotName = MutableStateFlow(savedStateHandle.get<String>("plotName") ?: "")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    private val _selectedVariety = MutableStateFlow(savedStateHandle.get<String>("selectedVariety") ?: "")
    val selectedVariety: StateFlow<String> = _selectedVariety.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _plotCoffeeVariety = MutableStateFlow<List<String>>(emptyList())
    val plotCoffeeVariety: StateFlow<List<String>> = _plotCoffeeVariety.asStateFlow()

    /**
     * Updates the plot name when the user modifies it.
     *
     * @param newName The new plot name entered by the user.
     */
    fun onPlotNameChange(newName: String) {
        _plotName.value = newName
        savedStateHandle["plotName"] = newName // Guardamos en SavedStateHandle
        if (newName.isNotBlank()) {
            _errorMessage.value = "" // Limpiar el mensaje de error si se ingresa un nombre válido
        }
    }
    /**
     * Updates the selected coffee variety when the user selects a new variety.
     *
     * @param newVariety The new coffee variety selected by the user.
     */
    fun onVarietyChange(newVariety: String) {
        _selectedVariety.value = newVariety
        savedStateHandle["selectedVariety"] = newVariety // Guardamos en SavedStateHandle
    }
    /**
     * Validates the input fields for creating plot information.
     *
     * @return `true` if the inputs are valid, `false` otherwise.
     */
    fun validateInputs(): Boolean {
        if (_plotName.value.isBlank()) {
            _errorMessage.value = "El nombre del lote no puede estar vacío."
            return false
        }
        return true
    }
    /**
     * Saves the plot information and navigates to the plot map view if validation is successful.
     *
     * @param navController The NavController for navigation.
     * @param farmId The ID of the farm to which the plot belongs.
     */
    fun saveAndNavigateToPlotMap(navController: NavController, farmId: Int) {
        if (!validateInputs()) {
            return
        }

        // Si la validación es exitosa, limpia el mensaje de error
        _errorMessage.value = ""

        // Navegar pasando farmId, plotName y selectedVariety codificados en la URL
        navController.navigate(
            "createMapPlotView/$farmId/${Uri.encode(_plotName.value)}/${Uri.encode(_selectedVariety.value)}"
        )
    }


    /**
     * Loads the available coffee varieties from SharedPreferences or other data sources.
     *
     * @param context The current context, needed for accessing SharedPreferences.
     */
    fun loadCoffeeVarieties(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val sharedPreferencesHelper = SharedPreferencesHelper(context)
            val varieties = sharedPreferencesHelper.getCoffeeVarieties() ?: listOf("Variedad 1", "Variedad 2")
            _plotCoffeeVariety.value = varieties
            _isLoading.value = false
        }
    }


}
