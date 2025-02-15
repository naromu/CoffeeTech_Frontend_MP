package com.example.coffetech.viewmodel.CulturalWorkTask

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.CreateCulturalWorkTaskRequest
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class ReminderViewModel : ViewModel() {

    private val _isReminderForUser = MutableStateFlow(false)
    val isReminderForUser: StateFlow<Boolean> = _isReminderForUser.asStateFlow()

    private val _isReminderForCollaborator = MutableStateFlow(false)
    val isReminderForCollaborator: StateFlow<Boolean> = _isReminderForCollaborator.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setReminderForUser(value: Boolean) {
        _isReminderForUser.value = value
    }

    fun setReminderForCollaborator(value: Boolean) {
        _isReminderForCollaborator.value = value
    }

    /**
     * Guarda los recordatorios configurados.
     *
     * @param plotId ID del lote.
     * @param culturalWorkType Tipo de labor cultural.
     * @param date Fecha de la tarea.
     * @param collaboratorUserId ID del colaborador.
     * @param navController Controlador de navegación para navegar entre pantallas.
     * @param context Contexto necesario para acceder al token de sesión.
     */
    fun saveReminders(
        plotId: Int,
        culturalWorkType: String,
        date: String,
        collaboratorUserId: Int,
        navController: NavController,
        context: Context
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener el token de sesión dentro de la función
                val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                if (sessionToken.isEmpty()) {
                    _errorMessage.value = "Token de sesión no encontrado."
                    return@launch
                }

                // Crear la solicitud
                val request = CreateCulturalWorkTaskRequest(
                    cultural_works_name = culturalWorkType,
                    plot_id = plotId,
                    reminder_owner = _isReminderForUser.value,
                    reminder_collaborator = _isReminderForCollaborator.value,
                    collaborator_user_id = collaboratorUserId,
                    task_date = date
                )

                // Realizar la solicitud a la API
                val response = RetrofitInstance.api.createCulturalWorkTask(sessionToken, request)
                if (response.status == "success") {
                    Toast.makeText(context, "Tarea Creada correctamente", Toast.LENGTH_SHORT).show()

                    // Navegar tres pantallas atrás
                    navController.popBackStack() // Volver una pantalla
                    navController.popBackStack() // Volver otra pantalla
                    navController.popBackStack() // Volver otra pantalla
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}