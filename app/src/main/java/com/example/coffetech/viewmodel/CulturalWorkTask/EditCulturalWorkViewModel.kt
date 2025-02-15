package com.example.coffetech.viewmodel.CulturalWorkTask

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.Collaborator
import com.example.coffetech.model.DeleteCulturalWorkTaskRequest
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.UpdateCulturalWorkTaskRequest
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditCulturalWorkViewModel : ViewModel() {

    // Estado para la lista de colaboradores
    private val _collaborators = MutableStateFlow<List<Collaborator>>(emptyList())
    val collaborators: StateFlow<List<Collaborator>> = _collaborators.asStateFlow()

    // Estado para el colaborador seleccionado
    private val _selectedCollaboratorId = MutableStateFlow<Int?>(null)
    val selectedCollaboratorId: StateFlow<Int?> = _selectedCollaboratorId.asStateFlow()

    // Estado para manejar la carga de colaboradores
    private val _isFetchingCollaborators = MutableStateFlow(false)
    val isFetchingCollaborators: StateFlow<Boolean> = _isFetchingCollaborators.asStateFlow()

    fun setSelectedCollaboratorId(id: Int, name: String) {
        _selectedCollaboratorId.value = id
        _collaboratorName.value = name
    }



    // Estado inicial recibido
    private var initialCulturalWorkTaskId: Int = 0
    private var initialCollaboratorUserId: Int = 0
    private var initialCulturalWorksName: String = ""
    private var initialCollaboratorName: String = ""
    private var initialTaskDate: String = ""

    // Estados actuales
    private val _selectedCulturalWork = MutableStateFlow("")
    val selectedCulturalWork: StateFlow<String> = _selectedCulturalWork.asStateFlow()

    // Estado para manejar mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _collaboratorName = MutableStateFlow("")
    val collaboratorName: StateFlow<String> = _collaboratorName.asStateFlow()

    private val _taskDate = MutableStateFlow("")
    val taskDate: StateFlow<String> = _taskDate.asStateFlow()

    // Estado de carga y formulario
    var isLoading = MutableStateFlow(false)
        private set

    var isFormSubmitted = MutableStateFlow(false)
        private set

    var showDeleteConfirmation = MutableStateFlow(false)

    // Estado para habilitar el botón de guardar
    private val _isSaveEnabled = MutableStateFlow(false)
    val isSaveEnabled: StateFlow<Boolean> = _isSaveEnabled.asStateFlow()

    init {
        // Observa cambios para habilitar el botón de guardar
        viewModelScope.launch {
            combine(
                _selectedCulturalWork,
                _collaboratorName,
                _taskDate
            ) { selectedWork, collaborator, date ->
                selectedWork != initialCulturalWorksName ||
                        collaborator != initialCollaboratorName ||
                        date != initialTaskDate
            }.collect { hasChanged ->
                _isSaveEnabled.value = hasChanged
            }
        }
    }

    fun fetchCollaborators(plotId: Int, context: Context) {
        viewModelScope.launch {
            _isFetchingCollaborators.value = true
            try {
                val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                val response = RetrofitInstance.api.getCollaboratorsWithCompletePermission(plotId, sessionToken)
                if (response.status == "success") {
                    _collaborators.value = response.data.collaborators
                    _selectedCollaboratorId.value = initialCollaboratorUserId
                } else {
                    _errorMessage.value = response.message
                    showErrorToast(context, response.message)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                showErrorToast(context, e.localizedMessage ?: "Error desconocido")
            } finally {
                _isFetchingCollaborators.value = false
            }
        }
    }


    // Inicializa los datos iniciales
    fun initialize(
        culturalWorkTaskId: Int,
        culturalWorksName: String,
        collaboratorUserId: Int,
        collaboratorName: String,
        taskDate: String,
        plotId: Int, // Añade plotId como parámetro
        context: Context
    ) {
        initialCulturalWorkTaskId = culturalWorkTaskId
        initialCulturalWorksName = culturalWorksName
        initialCollaboratorUserId = collaboratorUserId
        initialCollaboratorName = collaboratorName
        initialTaskDate = taskDate

        _selectedCulturalWork.value = culturalWorksName
        _collaboratorName.value = collaboratorName
        _taskDate.value = taskDate

        fetchCollaborators(plotId, context)

    }

    // Actualiza el tipo de labor cultural
    fun onTypeCulturalWorkChange(newType: String) {
        _selectedCulturalWork.value = newType
    }

    // Actualiza el nombre del colaborador
    fun onCollaboratorNameChange(newName: String) {
        _collaboratorName.value = newName
    }

    // Actualiza la fecha de la tarea
    fun onTaskDateChange(newDate: String) {
        _taskDate.value = newDate
    }

    // Guarda los cambios realizando una llamada a la API
    fun saveChanges(context: Context, navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            isFormSubmitted.value = true
            try {
                val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                if (sessionToken.isEmpty()) {
                    _errorMessage.value = "Token de sesión ausente."
                    showErrorToast(context, "Token de sesión ausente.")
                    isLoading.value = false
                    return@launch
                }

                val collaboratorId = _selectedCollaboratorId.value
                if (collaboratorId == null) {
                    _errorMessage.value = "Debe seleccionar un colaborador."
                    showErrorToast(context, "Debe seleccionar un colaborador.")
                    isLoading.value = false
                    return@launch
                }

                val request = UpdateCulturalWorkTaskRequest(
                    cultural_work_task_id = initialCulturalWorkTaskId,
                    cultural_works_name = _selectedCulturalWork.value,
                    collaborator_user_id = collaboratorId,
                    task_date = _taskDate.value
                )

                val response = RetrofitInstance.api.updateCulturalWorkTask(sessionToken, request)

                if (response.status == "success") {
                    Toast.makeText(context, "Tarea editada correctamente", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    _errorMessage.value = response.message
                    showErrorToast(context, response.message)
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                showErrorToast(context, e.localizedMessage ?: "Error desconocido")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun showErrorToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Elimina la tarea realizando una llamada a la API
    fun deleteTask(context: Context, navController: NavController) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: ""
                if (sessionToken.isEmpty()) {
                    isLoading.value = false
                    return@launch
                }

                val request = DeleteCulturalWorkTaskRequest(
                    cultural_work_task_id = initialCulturalWorkTaskId
                )

                val response = RetrofitInstance.api.deleteCulturalWorkTask(sessionToken, request)

                if (response.status == "success") {
                    Toast.makeText(context, "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    _errorMessage.value = response.message
                    showErrorToast(context, response.message)
                }

            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
                showErrorToast(context, e.localizedMessage ?: "Error desconocido")
            } finally {
                isLoading.value = false
            }
        }
    }
}