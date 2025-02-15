package com.example.coffetech.viewmodel.CulturalWorkTask

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes
import com.example.coffetech.model.Collaborator
import com.example.coffetech.model.CreateCulturalWorkTaskRequest
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddCulturalWorkViewModel2 : ViewModel() {

    // Estado para la lista de colaboradores
    private val _collaborators = MutableStateFlow<List<Collaborator>>(emptyList())
    val collaborators: StateFlow<List<Collaborator>> = _collaborators.asStateFlow()

    // Estado para el colaborador seleccionado
    private val _selectedCollaboratorId = MutableStateFlow<Int?>(null)
    val selectedCollaboratorId: StateFlow<Int?> = _selectedCollaboratorId.asStateFlow()

    // Estado para el texto del botón
    private val _buttonText = MutableStateFlow("Guardar")
    val buttonText: StateFlow<String> = _buttonText.asStateFlow()

    // Estado para manejar mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Estado para manejar la carga de colaboradores
    private val _isFetchingCollaborators = MutableStateFlow(false)
    val isFetchingCollaborators: StateFlow<Boolean> = _isFetchingCollaborators.asStateFlow()

    // Estado para manejar la carga de la solicitud
    private val _isSendingRequest = MutableStateFlow(false)
    val isSendingRequest: StateFlow<Boolean> = _isSendingRequest.asStateFlow()



    // Función para establecer el colaborador seleccionado
    fun setSelectedCollaboratorId(id: Int) {
        _selectedCollaboratorId.value = id
    }

    // Función para obtener los colaboradores
    fun fetchCollaborators(plotId: Int, context: Context) {
        viewModelScope.launch {
            _isFetchingCollaborators.value = true
            try {
                val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                val response = RetrofitInstance.api.getCollaboratorsWithCompletePermission(plotId, sessionToken)
                if (response.status == "success") {
                    _collaborators.value = response.data.collaborators
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isFetchingCollaborators.value = false
            }
        }
    }

    // Función para determinar el texto del botón basado en la fecha actual
    fun determineButtonText(taskDate: String) {
        viewModelScope.launch {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("America/Bogota")

                // Parsear la fecha de la tarea
                val taskDateParsed = sdf.parse(taskDate) ?: return@launch

                // Obtener la fecha actual en Bogotá sin la hora
                val currentCal = Calendar.getInstance(TimeZone.getTimeZone("America/Bogota"))
                val todayStr = sdf.format(currentCal.time)
                val today = sdf.parse(todayStr) ?: return@launch

                // Comparar las fechas
                val isTaskDateBeforeOrEqual = !taskDateParsed.after(today)

                // Determinar el texto del botón
                _buttonText.value = if (isTaskDateBeforeOrEqual) {
                    "Guardar"
                } else {
                    "Siguiente"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al determinar el texto del botón."
            }
        }
    }

    // Función para manejar el clic del botón
    fun onButtonClick(
        plotId: Int,
        culturalWorkType: String,
        date: String,
        plotName: String,
        navController: NavController,
        context: Context
    ) {
        viewModelScope.launch {
            if (_buttonText.value == "Guardar") {
                val collaboratorId = _selectedCollaboratorId.value
                if (collaboratorId == null) {
                    _errorMessage.value = "Debe seleccionar un colaborador."
                    return@launch
                }

                val request = CreateCulturalWorkTaskRequest(
                    cultural_works_name = culturalWorkType,
                    plot_id = plotId,
                    reminder_owner = false,
                    reminder_collaborator = false,
                    collaborator_user_id = collaboratorId,
                    task_date = date
                )

                _isSendingRequest.value = true
                try {
                    val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                    val response = RetrofitInstance.api.createCulturalWorkTask(sessionToken, request)
                    if (response.status == "success") {
                        // Navegar dos pantallas atrás
                        Toast.makeText(context, "Tarea Creada correctamente", Toast.LENGTH_SHORT).show()

                        navController.popBackStack() // Volver una pantalla
                        navController.popBackStack() // Volver otra pantalla
                    } else {
                        _errorMessage.value = response.message
                    }
                } catch (e: Exception) {
                    _errorMessage.value = e.localizedMessage
                } finally {
                    _isSendingRequest.value = false
                }

            } else {
                // Acción "Siguiente": navegar a otra ruta con los datos necesarios
                val collaboratorId = _selectedCollaboratorId.value
                if (collaboratorId == null) {
                    _errorMessage.value = "Debe seleccionar un colaborador."
                    return@launch
                }

                // Navegar con los parámetros necesarios
                navController.navigate(
                    "${Routes.ReminderCulturalWorkView}/$plotId/$plotName/$culturalWorkType/$date/$collaboratorId"
                )
            }
        }
    }
}
