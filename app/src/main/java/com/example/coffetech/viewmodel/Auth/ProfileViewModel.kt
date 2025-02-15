package com.example.coffetech.viewmodel.Auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.UpdateProfileRequest
import com.example.coffetech.model.UpdateProfileResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * ViewModel for managing the state and logic of the user's profile in the profile editing flow.
 * This ViewModel handles loading user data, updating the profile information, and managing error and loading states.
 */
class ProfileViewModel : ViewModel() {

    // State variables for managing user profile information, error messages, and loading status
    var name = mutableStateOf("")
        private set

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isProfileUpdated = mutableStateOf(false)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var nameErrorMessage = mutableStateOf("") // Error message for the "Name" field

    /**
     * Loads the user's profile data from SharedPreferences.
     *
     * @param context The [Context] used to access SharedPreferences.
     */
    fun loadUserData(context: Context) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        name.value = sharedPreferencesHelper.getUserName() // Load the user's name
        email.value = sharedPreferencesHelper.getUserEmail() // Load the user's email
    }

    /**
     * Handles changes to the name input field and marks the profile as updated.
     * Clears any error message if the name is valid.
     *
     * @param newName The new name entered by the user.
     */
    fun onNameChange(newName: String) {
        name.value = newName
        isProfileUpdated.value = true

        // Clear the error if the name is not blank
        if (newName.isNotBlank()) {
            nameErrorMessage.value = ""
        }
    }

    /**
     * Sends a request to update the user's profile information in the backend.
     * If successful, updates the SharedPreferences with the new name and shows a success message.
     *
     * @param context The [Context] used for displaying Toast messages and accessing SharedPreferences.
     * @param onSuccess A lambda function to be called upon successful profile update.
     */
    fun saveProfile(context: Context, onSuccess: () -> Unit) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        // Validate that the name field is not blank
        if (name.value.isBlank()) {
            nameErrorMessage.value = "El nombre no puede estar vacío"
            return
        }

        // Ensure the session token is available
        if (sessionToken == null) {
            errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(context, "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.", Toast.LENGTH_LONG).show()
            return
        }

        // Create the profile update request
        val updateRequest = UpdateProfileRequest(new_name = name.value)
        isLoading.value = true // Set loading state to true

        // Make the network request to update the profile
        RetrofitInstance.api.updateProfile(updateRequest, sessionToken).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                isLoading.value = false // Disable loading state

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == "success") {
                        // Save the new name in SharedPreferences
                        sharedPreferencesHelper.saveSessionData(sessionToken, name.value, email.value)

                        // Show a success message
                        Toast.makeText(context, "Perfil actualizado exitosamente.", Toast.LENGTH_LONG).show()
                        isProfileUpdated.value = false
                        onSuccess() // Call the success callback
                    } else {
                        val errorMsg = responseBody?.message ?: "Error desconocido al actualizar el perfil."
                        errorMessage.value = errorMsg
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    val serverErrorMsg = "Error al actualizar el perfil."
                    errorMessage.value = serverErrorMsg
                    Toast.makeText(context, serverErrorMsg, Toast.LENGTH_LONG).show()
                    Log.e("ProfileViewModel", "Error en la respuesta del servidor: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                val connectionErrorMsg = "Error de conexión"
                errorMessage.value = connectionErrorMsg
                Toast.makeText(context, connectionErrorMsg, Toast.LENGTH_LONG).show()
                Log.e("ProfileViewModel", "Error de conexión")
            }
        })
    }
}
