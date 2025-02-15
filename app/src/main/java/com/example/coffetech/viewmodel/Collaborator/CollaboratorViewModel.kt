package com.example.coffetech.viewmodel.Collaborator

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.coffetech.utils.SharedPreferencesHelper
import androidx.compose.runtime.State
import com.example.coffetech.model.ListCollaboratorResponse
import com.example.coffetech.model.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


data class Collaborator(
    val user_id: Int,
    val name: String,
    val email: String,
    val role: String, // Agrega también otras propiedades que necesitas
)

/**
 * ViewModel responsible for managing the state and logic of displaying and filtering collaborators.
 */
class CollaboratorViewModel : ViewModel() {

    // Lista original de colaboradores (sin filtrar)
    private val _allCollaborators = mutableListOf<Collaborator>()

    // Lista de colaboradores filtrada por la búsqueda
    private val _collaborators = MutableStateFlow<List<Collaborator>>(emptyList())
    val collaborators: StateFlow<List<Collaborator>> = _collaborators.asStateFlow()

    // Estado de búsqueda usando TextFieldValue
    private val _searchQuery = mutableStateOf(TextFieldValue(""))
    val searchQuery: MutableState<TextFieldValue> = _searchQuery

    private val _permissions = MutableStateFlow<List<String>>(emptyList())
    val permissions: StateFlow<List<String>> = _permissions.asStateFlow()

    // Estados de nombre, email, role y otros datos de colaborador

    private val _collaboratorName = MutableStateFlow("")
    val collaboratorName: StateFlow<String> = _collaboratorName.asStateFlow()

    private val _selectedRole = mutableStateOf<String?>(null)
    val selectedRole = _selectedRole

    // Estado del menú de dropdown
    private val _isDropdownExpanded = mutableStateOf(false)
    val isDropdownExpanded = _isDropdownExpanded

    // Estado para la lista de roles
    private val _roles = MutableStateFlow<List<String>>(emptyList())
    val roles: StateFlow<List<String>> = _roles.asStateFlow()

    // Estado de carga
    val isLoading = mutableStateOf(false)

    // Error
    val errorMessage = mutableStateOf("")

    val canEditAdministrador = hasPermission("edit_administrador_farm")
    val canEditOperador = hasPermission("edit_operador_farm")

    /**
     * Handles changes to the search query for filtering collaborators.
     *
     * @param query The new search query entered by the user.
     */
    fun onSearchQueryChanged(query: TextFieldValue) {
        _searchQuery.value = query
        filterCollaborators() // Aplica el filtrado combinado
    }

    /**
     * Selects a role for filtering collaborators.
     *
     * @param role The role selected by the user.
     */
    fun selectRole(role: String?) {
        _selectedRole.value = role
        filterCollaborators() // Aplica el filtrado combinado
    }

    /**
     * Filters the list of collaborators based on the search query and selected role.
     */
    private fun filterCollaborators() {
        val role = _selectedRole.value
        val query = _searchQuery.value.text

        // Filtramos primero por rol si hay uno seleccionado
        var filteredCollaborators = if (role != null) {
            _allCollaborators.filter { it.role == role }
        } else {
            _allCollaborators
        }

        // Luego aplicamos la búsqueda por nombre si hay un texto de búsqueda
        if (query.isNotBlank()) {
            filteredCollaborators = filteredCollaborators.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        // Actualizamos la lista de colaboradores visibles
        _collaborators.value = filteredCollaborators
    }


    /**
     * Loads the list of roles from SharedPreferences.
     *
     * @param context The current context, needed to access SharedPreferences.
     */
    fun loadRolesFromSharedPreferences(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()?.map { it.name } ?: emptyList()
        _roles.value = roles
    }


    /**
     * Sets the expansion state of the dropdown menu.
     *
     * @param isExpanded `true` if the dropdown menu is expanded, `false` otherwise.
     */
    fun setDropdownExpanded(isExpanded: Boolean) {
        _isDropdownExpanded.value = isExpanded
    }

    /**
     * Loads the permissions for the selected role from SharedPreferences.
     *
     * @param context The current context, needed to access SharedPreferences.
     * @param selectedRoleName The name of the role whose permissions are to be loaded.
     */
    fun loadRolePermissions(context: Context, selectedRoleName: String) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val roles = sharedPreferencesHelper.getRoles()

        roles?.find { it.name == selectedRoleName }?.let { role ->
            _permissions.value = role.permissions.map { it.name }
        }
    }
    /**
     * Checks if the user has a specific permission.
     *
     * @param permission The permission to check.
     * @return `true` if the user has the specified permission, `false` otherwise.
     */
    fun hasPermission(permission: String): Boolean {
        return _permissions.value.contains(permission)
    }

    /**
     * Loads the list of collaborators from the server.
     *
     * @param context The current context, needed for displaying toasts.
     * @param farmId The ID of the farm whose collaborators are being loaded.
     */
    fun loadCollaborators(context: Context, farmId: Int) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken == null) {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        isLoading.value = true

        RetrofitInstance.api.listCollaborators(farmId, sessionToken)
            .enqueue(object : Callback<ListCollaboratorResponse> {
                override fun onResponse(
                    call: Call<ListCollaboratorResponse>,
                    response: Response<ListCollaboratorResponse>
                ) {
                    isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            if (it.status == "success") {
                                val collaboratorsList = it.data.map { collaboratorResponse ->
                                    Collaborator(
                                        user_id = collaboratorResponse.user_id,
                                        name = collaboratorResponse.name,
                                        email = collaboratorResponse.email,
                                        role = collaboratorResponse.role
                                    )
                                }
                                Log.d("CollaboratorViewModel", "Lista de colaboradores recibida: $collaboratorsList")

                                _allCollaborators.clear()
                                _allCollaborators.addAll(collaboratorsList)
                                _collaborators.value =
                                    collaboratorsList // Mostrar todos los colaboradores al principio
                            } else {
                                errorMessage.value = it.message
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        errorMessage.value = "Error al obtener los colaboradores."
                        Log.e(
                            "CollaboratorViewModel",
                            "Error en la respuesta del servidor: ${response.errorBody()?.string()}"
                        )
                        Toast.makeText(
                            context,
                            "Error al obtener los colaboradores.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ListCollaboratorResponse>, t: Throwable) {
                    isLoading.value = false
                    errorMessage.value = "Error de conexión"
                    Log.e("CollaboratorViewModel", "Error de conexión")
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

}
