// viewmodel/healthcheck/EditResultHealthCheckViewModel.kt
package com.example.coffetech.viewmodel.healthcheck

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.GenericResponse
import com.example.coffetech.model.PredictionIdsRequest
import com.example.coffetech.model.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditResultHealthCheckViewModel : ViewModel() {

    // Estado para manejar mensajes de error
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    // Estado para manejar el indicador de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado para manejar el éxito de la eliminación
    private val _isDeleteSuccessful = MutableStateFlow(false)
    val isDeleteSuccessful: StateFlow<Boolean> = _isDeleteSuccessful.asStateFlow()

    /**
     * Elimina una detección realizando una solicitud al API.
     *
     * @param plotId ID del lote asociado (puede ser necesario para refrescar la lista en la vista principal).
     * @param sessionToken Token de sesión para autenticación.
     * @param detectionId ID de la detección a eliminar.
     */
    fun deleteDetection(
        context: Context,
        navController: NavController,
        sessionToken: String,
        detectionId: Int
    ) {
        Log.d("EditResultHCViewModel", "Iniciando eliminación de detección: $detectionId")
        _isLoading.value = true
        _errorMessage.value = ""

        val request = PredictionIdsRequest(prediction_ids = listOf(detectionId))

        RetrofitInstance.api.deleteDetection(sessionToken, request)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("EditResultHCViewModel", "Respuesta de eliminación: $responseBody")
                        if (responseBody?.status == "success") {
                            _isDeleteSuccessful.value = true

                            Log.d("EditResultHCViewModel", "Detección eliminada exitosamente.")

                            Toast.makeText(context, "Detección eliminada correctamente", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()

                        } else {
                            _errorMessage.value = responseBody?.message ?: "Error desconocido."
                            Log.e("EditResultHCViewModel", "Error de la API: ${responseBody?.message}")
                            Toast.makeText(context, "Error al eliminar detección ", Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        _errorMessage.value = "Error en la respuesta del servidor: ${response.code()}"
                        Log.e("EditResultHCViewModel", "Error en la respuesta del servidor: ${response.code()}")
                        Toast.makeText(context, "Error al eliminar detección ", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()

                    Log.e("EditResultHCViewModel", "Fallo en la llamada API: ${t.message}", t)
                }
            })
    }
}
