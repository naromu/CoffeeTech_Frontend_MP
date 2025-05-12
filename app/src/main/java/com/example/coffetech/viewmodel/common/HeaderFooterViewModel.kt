package com.example.coffetech.viewmodel.common

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.LogoutRequest
import com.example.coffetech.model.LogoutResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HeaderFooterViewModel : ViewModel() {
    private val _isMenuVisible = MutableStateFlow(false)
    val isMenuVisible: StateFlow<Boolean> = _isMenuVisible.asStateFlow()
    var isLoading = MutableStateFlow(false)
    fun toggleMenu() {
        _isMenuVisible.value = !_isMenuVisible.value
    }

    //Hamburger Functions
    fun onProfileClick(navController: NavController) {
        navController.navigate(Routes.ProfileView)
        toggleMenu() // Cierra el menú después de navegar
    }

    fun onNotificationsClick(navController: NavController) {
        navController.navigate(Routes.NotificationView)
        toggleMenu() // Cierra el menú después de navegar
    }


    fun onHelpClick() {}

    fun onLogoutClick(context: Context, navController: NavController) {
        val sharedPreferencesHelper = SharedPreferencesHelper(context)
        val sessionToken = sharedPreferencesHelper.getSessionToken()

        if (sessionToken == null) {
            Log.e("HeaderFooterViewModel", "No se encontró token de sesión")
            Toast.makeText(context, "Error: No se encontró sesión activa", Toast.LENGTH_LONG).show()
            return
        }

        isLoading.value = true

        val logoutRequest = LogoutRequest(session_token = sessionToken)
        AuthRetrofitInstance.api.logoutUser(logoutRequest).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful) {
                    val logoutResponse = response.body()
                    isLoading.value = false
                    if (logoutResponse?.status == "success") {
                        sharedPreferencesHelper.clearSession()
                        Log.d("HeaderFooterViewModel", "Logout exitoso")
                        Toast.makeText(context, "Sesión cerrada exitosamente", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.LoginView) {
                            popUpTo(Routes.FarmView) { inclusive = true }
                        }
                    } else {
                        Log.e("HeaderFooterViewModel", "Error en logout: ${logoutResponse?.message}")
                        Toast.makeText(context, "Error al cerrar sesión: ${logoutResponse?.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("HeaderFooterViewModel", "Error en la respuesta del servidor: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Error al comunicarse con el servidor", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Log.e("HeaderFooterViewModel", "Fallo en la conexión")
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
            }
        })
    }


    // Footer functions
    fun onHomeClick(navController: NavController) {
        navController.navigate(Routes.StartView) // Navegar a Home o la vista correspondiente
    }

    fun onFincasClick(navController: NavController) {
        navController.navigate(Routes.FarmView) // Navegar a la vista de Fincas
    }



}
