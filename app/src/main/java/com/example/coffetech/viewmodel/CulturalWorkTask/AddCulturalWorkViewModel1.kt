package com.example.coffetech.viewmodel.CulturalWorkTask

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class AddCulturalWorkViewModel1 : ViewModel() {

    private val _flowering_date = MutableStateFlow("")
    val flowering_date: StateFlow<String> = _flowering_date.asStateFlow()

    private val _typeCulturalWorkList = MutableStateFlow<List<String>>(emptyList())
    val typeCulturalWorkList: StateFlow<List<String>> = _typeCulturalWorkList.asStateFlow()

    private val _selectedTypeCulturalWork = MutableStateFlow("")
    val selectedTypeCulturalWork: StateFlow<String> = _selectedTypeCulturalWork.asStateFlow()

    var isLoading = MutableStateFlow(false)
        private set

    var isFormSubmitted = MutableStateFlow(false)
        private set

    private val _plotName = MutableStateFlow("")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    val isFormValid = MutableStateFlow(false)

    private fun validateForm() {
        isFormValid.value = _flowering_date.value.isNotBlank() && _selectedTypeCulturalWork.value.isNotBlank()
    }

    fun updateFloweringDate(date: String) {
        _flowering_date.value = date
        validateForm()
    }

    fun setTypeCulturalWorkList(culturalWorkList: List<String>) {
        _typeCulturalWorkList.value = culturalWorkList
    }

    fun setSelectedTypeCulturalWork(culturalWork: String) {
        _selectedTypeCulturalWork.value = culturalWork
        validateForm()
    }

    fun updatePlotName(name: String) {
        _plotName.value = name
    }

    fun submitForm() {
        isFormSubmitted.value = true
        validateForm()
    }
}
