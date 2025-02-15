package com.example.coffetech.viewmodel.flowering

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.GetRecommendationsResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecommendationFloweringViewModel: ViewModel() {

    private val _plotName = MutableStateFlow("")
    val plotName: StateFlow<String> = _plotName.asStateFlow()

    private val _flowering_date = MutableStateFlow("")
    val flowering_date: StateFlow<String> = _flowering_date.asStateFlow()

    // Lista completa de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _current_date = MutableStateFlow("")
    val current_date: StateFlow<String> = _current_date.asStateFlow()

    val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Función para obtener recomendaciones desde el servidor.
     */
    fun fetchRecommendations(floweringId: Int, sessionToken: String, context: Context) {
        _isLoading.value = true
        _errorMessage.value = ""

        RetrofitInstance.api.getRecommendations(floweringId, sessionToken)
            .enqueue(object : Callback<GetRecommendationsResponse> {
                override fun onResponse(
                    call: Call<GetRecommendationsResponse>,
                    response: Response<GetRecommendationsResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            if (it.status == "success") {
                                val recommendations = it.data.recommendations
                                _flowering_date.value = recommendations.flowering_date
                                _current_date.value = recommendations.current_date
                                _tasks.value = recommendations.tasks
                                _plotName.value = "" // Si necesitas obtener el nombre del lote, ajusta aquí
                            } else {
                                _errorMessage.value = it.message
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            try {
                                val errorJson = JSONObject(it)
                                val errorMsg = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al obtener recomendaciones."
                                }
                                _errorMessage.value = errorMsg
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                _errorMessage.value = "Error al procesar la respuesta del servidor."
                                Toast.makeText(
                                    context,
                                    "Error al procesar la respuesta del servidor.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } ?: run {
                            _errorMessage.value = "Respuesta vacía del servidor."
                            Toast.makeText(
                                context,
                                "Respuesta vacía del servidor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GetRecommendationsResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión"
                    Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}