package com.example.coffetech.viewmodel.collaborator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.NavController

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.example.coffetech.model.CreateInvitationRequest
import com.example.coffetech.model.CreateInvitationResponse
import com.example.coffetech.model.InvitationInstance

import com.example.coffetech.model.Role
import com.example.coffetech.utils.SharedPreferencesHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel responsible for managing the state and logic of adding a collaborator.
 */
class AddCollaboratorViewModel : ViewModel() {

    // Estados para los datos
    private val _collaboratorEmail = MutableStateFlow("")
    val collaboratorEmail: StateFlow<String> = _collaboratorEmail.asStateFlow()

    // Estado del menú de dropdown
    private val _isDropdownExpanded = mutableStateOf(false)
    val isDropdownExpanded = _isDropdownExpanded


    private val _collaboratorRole = MutableStateFlow<List<String>>(emptyList())
    val collaboratorRole: StateFlow<List<String>> = _collaboratorRole.asStateFlow()

    private val _selectedRole = MutableStateFlow("Seleccione un rol")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    var errorMessage = MutableStateFlow("")
        private set
    var isLoading = MutableStateFlow(false)
        private set

    private val _roleList = MutableStateFlow<List<Role>>(emptyList())  // guarda objetos Role completos
    val roleList: StateFlow<List<Role>> = _roleList

    private val _collaboratorRoleNames = MutableStateFlow<List<String>>(emptyList())  // solo los nombres para el dropdown
    val collaboratorRoleNames: StateFlow<List<String>> = _collaboratorRoleNames.asStateFlow()

    private val _selectedRoleName = MutableStateFlow("Seleccione un rol")
    val selectedRoleName: StateFlow<String> = _selectedRoleName.asStateFlow()

    private val _selectedRoleId = MutableStateFlow<Int?>(null)


    private val _permissions = MutableStateFlow<List<String>>(emptyList())
    /**
     * Updates the collaborator's email when the user modifies it.
     *
     * @param newName The new email entered by the user.
     */
    fun onCollaboratorEmailChange(newName: String) {
        _collaboratorEmail.value = newName
    }
    /**
     * Updates the collaborator's role when the user selects a new role.
     *
     * @param newRole The new role selected by the user.
     */
    fun onCollaboratorRoleChange(newRoleName: String) {
        _selectedRoleName.value = newRoleName
        val selectedRole = _roleList.value.find { it.name == newRoleName }
        if (selectedRole != null) {
            _selectedRoleId.value = selectedRole.role_id
            Log.d("AddCollaboratorVM", "Rol seleccionado: ${selectedRole.name} (ID: ${selectedRole.role_id})")
        } else {
            Log.e("AddCollaboratorVM", "No se encontró el rol con nombre: $newRoleName")
            _selectedRoleId.value = null
        }
    }

    /**
     * Loads the available roles from SharedPreferences.
     *
     * @param context The current context, needed to access SharedPreferences.
     */
    fun loadRolesFromSharedPreferences(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()?.map { it.name } ?: emptyList()
        _collaboratorRole.value = roles
    }

    /**
     * Loads the roles available for the collaborator based on the user's role.
     *
     * @param context The current context, needed to access SharedPreferences.
     * @param userRole The role of the current user.
     */
    fun loadRolesForCollaborator(context: Context, userRole: String) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()
        _roleList.value = roles ?: emptyList()

        val allowedRoles = mutableListOf<Role>()

        roles?.find { it.name == userRole }?.let { currentRole ->
            _permissions.value = currentRole.permissions.map { it.name }

            if (_permissions.value.contains("add_administrator_farm")) {
                roles.find { it.name == "Administrador de finca" }?.let { allowedRoles.add(it) }
            }
            if (_permissions.value.contains("add_operator_farm")) {
                roles.find { it.name == "Operador de campo" }?.let { allowedRoles.add(it) }
            }
        }

        _roleList.value = allowedRoles
        _collaboratorRoleNames.value = allowedRoles.map { it.name }
    }


    /**
     * Validates the input fields for adding a collaborator.
     *
     * @return `true` if the inputs are valid, `false` otherwise.
     */
    fun validateInputs(): Boolean {
        if (_collaboratorEmail.value.isBlank()) {
            errorMessage.value = "El correo del colaborador no puede estar vacío."
            return false
        }

        // Valida que sea un formato de correo válido (opcional)
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_collaboratorEmail.value).matches()) {
            errorMessage.value = "El correo del colaborador no es válido."
            return false
        }

        // Validación del rol seleccionado
        if (_selectedRoleName.value == "Seleccione un rol") {
            errorMessage.value = "Debe seleccionar un rol válido."
            return false
        }

        errorMessage.value = ""  // Limpiar el mensaje de error si no hay problemas
        return true
    }

    /**
     * Initiates the process of creating a collaborator invitation.
     *
     * @param navController The NavController for navigation.
     * @param context The current context, needed for displaying toasts.
     * @param farmId The ID of the farm to which the collaborator is being added.
     */
    fun onCreate(navController: NavController, context: Context, farmId: Int) {
        if (!validateInputs()) {
            return
        }

        errorMessage.value = ""
        isLoading.value = true

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken() ?: run {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            isLoading.value = false
            return
        }

        // Crear el objeto de solicitud
        val createInvitationRequest = CreateInvitationRequest(
            email = _collaboratorEmail.value,
            suggested_role_id = _selectedRoleId.value ?: throw IllegalStateException("Debe seleccionar un rol"),
            farm_id = farmId
        )
        Log.d("AddCollaboratorViewModel", "CreateInvitationRequest: $createInvitationRequest")




        // Realizar la solicitud al servidor
        InvitationInstance.api.createInvitation(sessionToken, createInvitationRequest).enqueue(object :
            Callback<CreateInvitationResponse> {
            override fun onResponse(call: Call<CreateInvitationResponse>, response: Response<CreateInvitationResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if (it.status == "success") {
                            Toast.makeText(context, "Invitación creada exitosamente", Toast.LENGTH_LONG).show()
                            navController.popBackStack() // Regresar a la pantalla anterior
                        } else {
                            errorMessage.value = it.message
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        try {
                            val errorJson = JSONObject(it)
                            val errorMessage = if (errorJson.has("message")) {
                                errorJson.getString("message")
                            } else {
                                "Error desconocido al crear la invitación."
                            }
                            this@AddCollaboratorViewModel.errorMessage.value = errorMessage
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            this@AddCollaboratorViewModel.errorMessage.value = "Error al procesar la respuesta del servidor."
                            Toast.makeText(context, "Error al procesar la respuesta del servidor.", Toast.LENGTH_LONG).show()
                        }
                    } ?: run {
                        this@AddCollaboratorViewModel.errorMessage.value = "Respuesta vacía del servidor."
                        Toast.makeText(context, "Respuesta vacía del servidor.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<CreateInvitationResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error de conexión"
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }




}



