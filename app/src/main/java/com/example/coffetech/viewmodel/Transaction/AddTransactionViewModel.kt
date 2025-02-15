// AddTransactionViewModel.kt
package com.example.coffetech.viewmodel.Transaction

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.CreateTransactionRequest
import com.example.coffetech.model.CreateTransactionResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.utils.SharedPreferencesHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.TimeZone.*

// AddTransactionViewModel.kt
class AddTransactionViewModel : ViewModel() {

    // Estados para los campos del formulario
    private val _selectedTransactionType = MutableStateFlow("")
    val selectedTransactionType: StateFlow<String> = _selectedTransactionType.asStateFlow()

    private val _transactionTypes = MutableStateFlow(listOf("Ingreso", "Gasto"))
    val transactionTypes: StateFlow<List<String>> = _transactionTypes.asStateFlow()

    private val _selectedTransactionCategory = MutableStateFlow("")
    val selectedTransactionCategory: StateFlow<String> = _selectedTransactionCategory.asStateFlow()

    private val _transactionCategories = MutableStateFlow<List<String>>(emptyList())
    val transactionCategories: StateFlow<List<String>> = _transactionCategories.asStateFlow()

    private val _valor = MutableStateFlow("")
    val valor: StateFlow<String> = _valor.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    private val _fecha = MutableStateFlow("")
    val fecha: StateFlow<String> = _fecha.asStateFlow()

    // Estados para mensajes de error específicos
    private val _transactionTypeError = MutableStateFlow<String?>(null)
    val transactionTypeError: StateFlow<String?> = _transactionTypeError.asStateFlow()

    private val _transactionCategoryError = MutableStateFlow<String?>(null)
    val transactionCategoryError: StateFlow<String?> = _transactionCategoryError.asStateFlow()

    private val _valorError = MutableStateFlow<String?>(null)
    val valorError: StateFlow<String?> = _valorError.asStateFlow()

    private val _descripcionError = MutableStateFlow<String?>(null)
    val descripcionError: StateFlow<String?> = _descripcionError.asStateFlow()

    private val _fechaError = MutableStateFlow<String?>(null)
    val fechaError: StateFlow<String?> = _fechaError.asStateFlow()

    // Estado para manejar la habilitación del botón "Crear"
    private val _isButtonEnabled = MutableStateFlow(false)
    val isButtonEnabled: StateFlow<Boolean> = _isButtonEnabled.asStateFlow()

    // Estados de carga y mensaje de error general
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    // Indica si el formulario ha sido enviado para mostrar errores
    private val _isFormSubmitted = MutableStateFlow(false)
    val isFormSubmitted: StateFlow<Boolean> = _isFormSubmitted.asStateFlow()

    init {
        viewModelScope.launch {
            // Validación de los campos
            val fieldsValidFlow = combine(
                _selectedTransactionType,
                _selectedTransactionCategory,
                _valor,
                _fecha
            ) { transactionType, category, valor, fecha ->
                transactionType.isNotBlank() && category.isNotBlank() && valor.isNotBlank() && fecha.isNotBlank()
            }

            val errorsValidFlow = combine(
                _transactionTypeError,
                _transactionCategoryError,
                _valorError,
                _fechaError
            ) { typeError, categoryError, valorErr, dateErr ->
                typeError == null && categoryError == null && valorErr == null && dateErr == null
            }

            combine(fieldsValidFlow, errorsValidFlow) { fieldsValid, errorsValid ->
                fieldsValid && errorsValid
            }.collect { isValid ->
                _isButtonEnabled.value = isValid
            }
        }
    }

    // Funciones para actualizar los campos y validar en tiempo real
    fun onTransactionTypeChange(newType: String) {
        _selectedTransactionType.value = newType
        if (newType.isNotBlank()) {
            _transactionTypeError.value = null
        } else {
            _transactionTypeError.value = "Seleccione tipo de transacción."
        }

        // Actualizar las categorías basadas en el tipo
        _transactionCategories.value = when (newType) {
            "Ingreso" -> listOf("Venta de café", "Otros")
            "Gasto" -> listOf("Pagos a colaboradores", "Fertilizantes", "Plaguicidas/herbicidas", "Otros")
            else -> emptyList()
        }

        // Resetear la categoría seleccionada
        _selectedTransactionCategory.value = ""
    }

    fun onTransactionCategoryChange(newCategory: String) {
        _selectedTransactionCategory.value = newCategory
        if (newCategory.isNotBlank()) {
            _transactionCategoryError.value = null
        } else {
            _transactionCategoryError.value = "Seleccione categoría de transacción."
        }
    }

    fun onValorChange(newValor: String) {
        _valor.value = newValor
        if (newValor.isNotBlank()) {
            val valorDouble = newValor.toDoubleOrNull()
            _valorError.value = when {
                valorDouble == null -> "El valor debe ser un número válido."
                valorDouble <= 0 -> "El valor debe ser un número positivo."
                else -> null
            }
        } else {
            _valorError.value = "El valor es obligatorio."
        }
    }

    fun onDescripcionChange(newDescripcion: String) {
        _descripcion.value = newDescripcion
    }

    fun onFechaChange(newFecha: String) {
        _fecha.value = newFecha
        _fechaError.value = when {
            newFecha.isBlank() -> "La fecha es obligatoria."
            isFutureDate(newFecha) -> "La fecha no puede estar en el futuro."
            else -> null
        }
    }

    // Función para verificar si la fecha es futura
    private fun isFutureDate(dateStr: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return try {
            val date = sdf.parse(dateStr)
            val today = Calendar.getInstance()
            today.set(Calendar.HOUR_OF_DAY, 0)
            today.set(Calendar.MINUTE, 0)
            today.set(Calendar.SECOND, 0)
            today.set(Calendar.MILLISECOND, 0)
            date?.after(today.time) ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Función para validar el formulario antes de crear la transacción
    fun validateForm(): Boolean {
        var isValid = true

        if (_selectedTransactionType.value.isBlank()) {
            _transactionTypeError.value = "Seleccione tipo de transacción."
            isValid = false
        }

        if (_selectedTransactionCategory.value.isBlank()) {
            _transactionCategoryError.value = "Seleccione categoría de transacción."
            isValid = false
        }

        if (_valor.value.isBlank()) {
            _valorError.value = "El valor es obligatorio."
            isValid = false
        } else {
            val valorDouble = _valor.value.toDoubleOrNull()
            if (valorDouble == null || valorDouble <= 0) {
                _valorError.value = "El valor debe ser un número positivo."
                isValid = false
            }
        }

        if (_fecha.value.isBlank()) {
            _fechaError.value = "La fecha es obligatoria."
            isValid = false
        } else if (isFutureDate(_fecha.value)) {
            _fechaError.value = "La fecha no puede estar en el futuro."
            isValid = false
        }

        _isFormSubmitted.value = true

        return isValid
    }

    // Función para crear la transacción
    fun onCreate(navController: NavController, context: Context, plotId: Int) {
        viewModelScope.launch {
            if (validateForm()) {
                _isLoading.value = true
                _errorMessage.value = ""
                try {
                    val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: run {
                        _errorMessage.value = "No se encontró el token de sesión."
                        Toast.makeText(
                            context,
                            "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                            Toast.LENGTH_LONG
                        ).show()
                        _isLoading.value = false
                        return@launch
                    }

                    val request = CreateTransactionRequest(
                        plot_id = plotId,
                        transaction_type_name = _selectedTransactionType.value,
                        transaction_category_name = _selectedTransactionCategory.value, // Incluir la categoría
                        description = _descripcion.value,
                        value = _valor.value.toLong(),
                        transaction_date = _fecha.value
                    )

                    RetrofitInstance.api.createTransaction(
                        sessionToken = sessionToken,
                        request = request
                    ).enqueue(object : Callback<CreateTransactionResponse> {
                        override fun onResponse(
                            call: Call<CreateTransactionResponse>,
                            response: Response<CreateTransactionResponse>
                        ) {
                            _isLoading.value = false
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody?.status == "success") {
                                    Toast.makeText(
                                        context,
                                        "Transacción creada correctamente.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.popBackStack()
                                } else {
                                    _errorMessage.value = responseBody?.message ?: "Error al crear la transacción."
                                    Log.e("AddTransactionViewModel", "Error: ${responseBody?.message}")
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                errorBody?.let {
                                    try {
                                        val errorJson = JSONObject(it)
                                        val errorMsg = if (errorJson.has("message")) {
                                            errorJson.getString("message")
                                        } else {
                                            "Error desconocido al crear la transacción."
                                        }
                                        _errorMessage.value = errorMsg
                                        Log.e("AddTransactionViewModel", errorMsg)
                                    } catch (e: Exception) {
                                        _errorMessage.value = "Error al procesar la respuesta del servidor."
                                        Log.e("AddTransactionViewModel", "JSON parsing error", e)
                                    }
                                } ?: run {
                                    _errorMessage.value = "Error al crear la transacción: respuesta vacía del servidor."
                                    Log.e("AddTransactionViewModel", _errorMessage.value)
                                }
                            }
                        }

                        override fun onFailure(call: Call<CreateTransactionResponse>, t: Throwable) {
                            _isLoading.value = false
                            _errorMessage.value = "Error de conexión al crear la transacción."
                            Log.e("AddTransactionViewModel", "API call failed: ${t.message}", t)
                        }
                    })
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = "Error al crear la transacción: ${e.message}"
                    Log.e("AddTransactionViewModel", "Error: ${e.message}", e)
                }
            }
        }
    }
}
