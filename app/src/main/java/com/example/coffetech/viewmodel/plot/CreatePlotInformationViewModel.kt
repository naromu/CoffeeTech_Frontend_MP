package com.example.coffetech.viewmodel.plot

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.CoffeeVariety
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the state and logic of creating plot information,
 * including handling plot name and coffee variety selection.
 */
class CreatePlotInformationViewModel(
    private val savedStateHandle: SavedStateHandle // Agregamos SavedStateHandle
) : ViewModel() {

    private val _coffeeVarietyList = MutableStateFlow<List<CoffeeVariety>>(emptyList())
    val coffeeVarietyList: StateFlow<List<CoffeeVariety>> = _coffeeVarietyList

    private val _coffeeVarietyNames = MutableStateFlow<List<String>>(emptyList())
    val coffeeVarietyNames: StateFlow<List<String>> = _coffeeVarietyNames.asStateFlow()

    private val _selectedVarietyId = MutableStateFlow<Int?>(null)
    val selectedVarietyId: StateFlow<Int?> = _selectedVarietyId.asStateFlow()

    private val _selectedVarietyName = MutableStateFlow("Seleccione una variedad")
    val selectedVarietyName: StateFlow<String> = _selectedVarietyName.asStateFlow()


    // Usamos SavedStateHandle para mantener el estado
    private val _plotName = MutableStateFlow(savedStateHandle.get<String>("plotName") ?: "")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
    fun onVarietyChange(newVarietyName: String) {
        _selectedVarietyName.value = newVarietyName
        savedStateHandle["selectedVariety"] = newVarietyName

        val selectedVariety = _coffeeVarietyList.value.find { it.name == newVarietyName }
        _selectedVarietyId.value = selectedVariety?.coffee_variety_id
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
        if (!validateInputs() || _selectedVarietyId.value == null) {
            _errorMessage.value = "Debe seleccionar una variedad válida."
            return
        }

        _errorMessage.value = ""

        navController.navigate(
            "createMapPlotView/$farmId/${Uri.encode(_plotName.value)}/${_selectedVarietyId.value}"
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
            val varieties = sharedPreferencesHelper.getCoffeeVarieties() ?: emptyList()
            _coffeeVarietyList.value = varieties
            _coffeeVarietyNames.value = varieties.map { it.name }  // aquí sí usamos .name
            _isLoading.value = false
        }
    }





}
