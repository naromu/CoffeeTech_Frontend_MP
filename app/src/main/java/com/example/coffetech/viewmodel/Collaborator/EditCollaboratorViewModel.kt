package com.example.coffetech.viewmodel.Collaborator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.navigation.NavController
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.coffetech.model.DeleteCollaboratorRequest
import com.example.coffetech.model.DeleteCollaboratorResponse
import com.example.coffetech.model.EditCollaboratorRequest
import com.example.coffetech.model.EditCollaboratorResponse
import com.example.coffetech.model.FarmInstance
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.Role
import com.example.coffetech.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * ViewModel responsible for managing the state and logic of editing a collaborator.
 */
class EditCollaboratorViewModel : ViewModel() {

    // Estado del rol seleccionado
    private val _collaboratorRole = MutableStateFlow<List<String>>(emptyList())
    val collaboratorRole: StateFlow<List<String>> = _collaboratorRole.asStateFlow()

    private val _selectedRole = MutableStateFlow("Seleccione un rol")
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    var errorMessage = MutableStateFlow("")
        private set
    var isLoading = MutableStateFlow(false)
        private set

    private val _roleList = MutableStateFlow<List<Role>>(emptyList())
    val roleList: StateFlow<List<Role>> = _roleList

    private val _selectedRoleName = MutableStateFlow("Seleccione un rol")
    val selectedRoleName: StateFlow<String> = _selectedRoleName.asStateFlow()

    private val _selectedRoleId = MutableStateFlow<Int?>(null)


    // Estado para rastrear si hay cambios pendientes
    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    // Guardar valores iniciales para comparación
    private var initialSelectedRole = ""


    private val _permissions = MutableStateFlow<List<String>>(emptyList())

    /**
     * Loads the roles available for editing based on the user's role.
     *
     * @param context The current context, needed to access SharedPreferences.
     * @param userRole The role of the current user.
     */
    fun loadRolesForEditing(context: Context, userRole: String) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()

        _roleList.value = roles ?: emptyList()

        roles?.find { it.name == userRole }?.let { role ->
            _permissions.value = role.permissions.map { it.name }

            val allowedRoles = mutableListOf<Role>()
            if (_permissions.value.contains("edit_administrator_farm")) {
                roles.find { it.name == "Administrador de finca" }?.let { allowedRoles.add(it) }
            }
            if (_permissions.value.contains("edit_operator_farm")) {
                roles.find { it.name == "Operador de campo" }?.let { allowedRoles.add(it) }
            }

            _roleList.value = allowedRoles
            _collaboratorRole.value = allowedRoles.map { it.name }
        }
    }


    /**
     * Initializes the ViewModel with the selected role of the collaborator.
     *
     * @param selectedRole The initial role of the collaborator.
     */
    fun initializeValues(selectedRole: String) {
        initialSelectedRole = selectedRole
        _selectedRole.value = selectedRole
    }

    /**
     * Validates the input fields for editing a collaborator.
     *
     * @return `true` if the inputs are valid, `false` otherwise.
     */
    private fun validateInputs(): Boolean {
        if (_selectedRole.value == "Seleccione un rol") {
            errorMessage.value = "Debe seleccionar una opción válida para el rol."
            return false
        }

        return true
    }

    /**
     * Loads the list of roles from SharedPreferences.
     *
     * @param context The current context, needed to access SharedPreferences.
     */
    fun loadRolesFromSharedPreferences(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()?.map { it.name } ?: emptyList()
        _collaboratorRole.value = roles
    }

    /**
     * Initiates the process of editing a collaborator.
     *
     * @param context The current context, needed for displaying toasts.
     * @param farmId The ID of the farm to which the collaborator belongs.
     * @param collaboratorId The ID of the collaborator to be edited.
     * @param navController The NavController for navigation.
     */
    fun editCollaborator(context: Context, farmId: Int, collaboratorId: Int, navController: NavController) {
        if (!validateInputs()) return

        val roleId = _selectedRoleId.value
        if (roleId == null) {
            errorMessage.value = "Debe seleccionar un rol válido."
            Toast.makeText(context, "Debe seleccionar un rol válido.", Toast.LENGTH_LONG).show()
            return
        }

        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken == null) {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            return
        }

        isLoading.value = true

        // Crear objeto de solicitud para editar el colaborador
        val request = EditCollaboratorRequest(
            collaborator_id = collaboratorId,
            new_role_id = roleId
        )
        Log.d("EditCollaboratorVM", "farmId: ${farmId}, sessionToken: ${sessionToken}, CollaboratorID: ${collaboratorId} ,role_id : ${roleId} )")

        // Llamar a la API para editar el colaborador
        FarmInstance.api.editCollaboratorRole(farmId, sessionToken, request).enqueue(object : Callback<EditCollaboratorResponse> {
            override fun onResponse(call: Call<EditCollaboratorResponse>, response: Response<EditCollaboratorResponse>) {
                isLoading.value = false
                Log.d("EditCollaborator", "Response code: ${response.code()}")
                Log.d("EditCollaborator", "Response body: ${response.body()}")
                Log.d("EditCollaborator", "Response errorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        Log.d("EditCollaborator", "API status: ${it.status}, message: ${it.message}")

                        if (it.status == "success") {
                            Toast.makeText(context, "Colaborador actualizado correctamente.", Toast.LENGTH_LONG).show()
                            navController.popBackStack() // Regresar a la pantalla anterior
                        } else {
                            errorMessage.value = it.message
                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    errorMessage.value = "Error al actualizar el colaborador."
                    Toast.makeText(context, "Error al actualizar el colaborador.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<EditCollaboratorResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error de conexión"
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * Handles changes to the collaborator's role.
     *
     * @param newRole The new role selected by the user.
     */
    fun onCollaboratorRoleChange(newRole: String) {
        _selectedRoleName.value = newRole
        _selectedRole.value = newRole // opcional, si sigues usando este elsewhere

        val selectedRole = _roleList.value.find { it.name == newRole }
        if (selectedRole != null) {
            _selectedRoleId.value = selectedRole.role_id
            Log.d("EditCollaboratorVM", "Rol seleccionado: ${selectedRole.name} (ID: ${selectedRole.role_id})")
        } else {
            Log.e("EditCollaboratorVM", "No se encontró el rol con nombre: $newRole")
            _selectedRoleId.value = null
        }

        checkForChanges()
    }


    /**
     * Checks if there are any changes made to the collaborator's role.
     */
    private fun checkForChanges() {
        _hasChanges.value = _selectedRole.value != initialSelectedRole
    }

    /**
     * Initiates the process of deleting a collaborator.
     *
     * @param context The current context, needed for displaying toasts.
     * @param farmId The ID of the farm to which the collaborator belongs.
     * @param collaboratorId The ID of the collaborator to be deleted.
     * @param navController The NavController for navigation.
     */
    fun deleteCollaborator(context: Context, farmId: Int, collaboratorId: Int, navController: NavController) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken == null) {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            return
        }

        isLoading.value = true

        // Crear objeto de solicitud para eliminar el colaborador
        val requestBody = mapOf("collaborator_user_id" to collaboratorId)

        // Crear objeto de solicitud para eliminar el colaborador
        val request = DeleteCollaboratorRequest(
            collaborator_id = collaboratorId
        )
        Log.d("EditCollaboratorVM", "farmId: ${farmId}, sessionToken: ${sessionToken}, collaborator_user_role_id : ${collaboratorId} )")


        // Llamar a la API para eliminar el colaborador

        FarmInstance.api.deleteCollaborator(farmId, sessionToken, request).enqueue(object : Callback<DeleteCollaboratorResponse> {
            override fun onResponse(call: Call<DeleteCollaboratorResponse>, response: Response<DeleteCollaboratorResponse>) {
                isLoading.value = false
                Log.d("EditCollaborator", "Response code: ${response.code()}")
                Log.d("EditCollaborator", "Response body: ${response.body()}")
                Log.d("EditCollaborator", "Response errorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    Toast.makeText(context, "Colaborador eliminado correctamente.", Toast.LENGTH_LONG).show()
                    navController.popBackStack() // Regresar a la pantalla anterior
                } else {
                    errorMessage.value = "Error al eliminar el colaborador."
                    Toast.makeText(context, "Error al eliminar el colaborador.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<DeleteCollaboratorResponse>, t: Throwable) {
                isLoading.value = false
                errorMessage.value = "Error de conexión"
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }

}
