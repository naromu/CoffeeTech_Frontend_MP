// EditTransactionViewModel.kt
package com.example.coffetech.viewmodel.Transaction

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.coffetech.model.EditTransactionRequest
import com.example.coffetech.model.EditTransactionResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.Transaction
import com.example.coffetech.model.TransactionCategory
import com.example.coffetech.model.TransactionCategoryResponse
import com.example.coffetech.model.TransactionDeleteRequest
import com.example.coffetech.model.TransactionDeleteResponse
import com.example.coffetech.model.TransactionInstance
import com.example.coffetech.model.TransactionType
import com.example.coffetech.model.TransactionTypeResponse
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

class EditTransactionViewModel : ViewModel() {

    private var originalTransactionType = ""
    private var originalTransactionCategory = ""
    private var originalValor = ""
    private var originalDescripcion = ""
    private var originalFecha = ""

    // Estados para los campos del formulario
    private val _transactionId = MutableStateFlow(0)
    val transactionId: StateFlow<Int> = _transactionId.asStateFlow()

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

    // Estado para manejar la habilitación del botón "Guardar"
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

    private val _transactionTypeList = MutableStateFlow<List<TransactionType>>(emptyList())
    private val _transactionCategoryList = MutableStateFlow<List<TransactionCategory>>(emptyList())

    private val _selectedTransactionTypeId = MutableStateFlow<Int?>(null)
    private val _selectedTransactionCategoryId = MutableStateFlow<Int?>(null)

    fun loadTransactionTypesAndCategories() {
        // Cargar tipos
        TransactionInstance.api.getTransactionTypes().enqueue(object : Callback<TransactionTypeResponse> {
            override fun onResponse(call: Call<TransactionTypeResponse>, response: Response<TransactionTypeResponse>) {
                response.body()?.data?.transaction_types?.let { types ->
                    _transactionTypeList.value = types
                    _transactionTypes.value = types.map { it.name }
                }
            }

            override fun onFailure(call: Call<TransactionTypeResponse>, t: Throwable) {
                Log.e("AddTransactionVM", "Error cargando tipos", t)
            }
        })

        // Cargar categorías
        TransactionInstance.api.getTransactionCategories().enqueue(object : Callback<TransactionCategoryResponse> {
            override fun onResponse(call: Call<TransactionCategoryResponse>, response: Response<TransactionCategoryResponse>) {
                response.body()?.data?.transaction_categories?.let { categories ->
                    _transactionCategoryList.value = categories
                }
            }

            override fun onFailure(call: Call<TransactionCategoryResponse>, t: Throwable) {
                Log.e("AddTransactionVM", "Error cargando categorías", t)
            }
        })
    }


    // Función para cargar la transacción existente
    fun loadTransactionData(
        transactionId: Int,
        transactionTypeName: String,
        transactionCategoryName: String,
        description: String?,
        value: Long,
        transactionDate: String
    ) {
        // Establecer los valores iniciales
        _transactionId.value = transactionId
        _selectedTransactionType.value = transactionTypeName
        _selectedTransactionCategory.value = transactionCategoryName
        _valor.value = value.toString()
        _descripcion.value = description ?: ""
        _fecha.value = transactionDate

        // Guardar los valores originales para comparación
        originalTransactionType = transactionTypeName
        originalTransactionCategory = transactionCategoryName
        originalValor = value.toString()
        originalDescripcion = description ?: ""
        originalFecha = transactionDate

        // 🔄 Asignar IDs luego de que las listas hayan sido cargadas
        viewModelScope.launch {
            // Esperar hasta que se hayan cargado los tipos y categorías
            combine(
                _transactionTypeList,
                _transactionCategoryList
            ) { typeList, categoryList -> typeList.isNotEmpty() && categoryList.isNotEmpty() }
                .filter { it } // Solo continuar cuando ambas listas estén cargadas
                .first() // Solo una vez

            // Asignar IDs
            _selectedTransactionTypeId.value =
                _transactionTypeList.value.find { it.name == transactionTypeName }?.transaction_type_id

            _selectedTransactionCategoryId.value =
                _transactionCategoryList.value.find { it.name == transactionCategoryName }?.transaction_category_id

            // Filtrar categorías del tipo seleccionado
            val filteredCategories = _transactionCategoryList.value.filter {
                it.transaction_type_name == transactionTypeName
            }
            _transactionCategories.value = filteredCategories.map { it.name }

            // Activar comparación para habilitar el botón
            combine(
                _selectedTransactionType,
                _selectedTransactionCategory,
                _valor,
                _descripcion,
                _fecha
            ) { type, category, valor, descripcion, fecha ->
                type != originalTransactionType ||
                        category != originalTransactionCategory ||
                        valor != originalValor ||
                        descripcion != originalDescripcion ||
                        fecha != originalFecha
            }.collect { hasChanges ->
                _isButtonEnabled.value = hasChanges
            }
        }
    }

    // Funciones para actualizar los campos y validar en tiempo real
    fun onTransactionTypeChange(newType: String) {
        _selectedTransactionType.value = newType
        val selected = _transactionTypeList.value.find { it.name == newType }
        _selectedTransactionTypeId.value = selected?.transaction_type_id

        // Filtrar categorías asociadas al tipo
        val filtered = _transactionCategoryList.value.filter {
            it.transaction_type_id == selected?.transaction_type_id
        }

        _transactionCategories.value = filtered.map { it.name }
        _selectedTransactionCategory.value = ""
        _selectedTransactionCategoryId.value = null

        _transactionTypeError.value = if (newType.isNotBlank()) null else "Seleccione tipo de transacción."
    }


    fun onTransactionCategoryChange(newCategory: String) {
        _selectedTransactionCategory.value = newCategory
        val selected = _transactionCategoryList.value.find { it.name == newCategory }
        _selectedTransactionCategoryId.value = selected?.transaction_category_id

        _transactionCategoryError.value = if (newCategory.isNotBlank()) null else "Seleccione categoría de transacción."
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
        // No hay validación específica para la descripción
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

    // Función para validar el formulario antes de guardar la transacción
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

    // Función para guardar la transacción editada
    fun onSave(navController: NavController, context: Context) {
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

                    val request = EditTransactionRequest(
                        transaction_id = _transactionId.value,
                        transaction_category_id = _selectedTransactionCategoryId.value ?: return@launch,
                        description = _descripcion.value,
                        value = _valor.value.toLong(),
                        transaction_date = _fecha.value
                    )


                    TransactionInstance.api.editTransaction(
                        sessionToken = sessionToken,
                        request = request
                    ).enqueue(object : Callback<EditTransactionResponse> {
                        override fun onResponse(
                            call: Call<EditTransactionResponse>,
                            response: Response<EditTransactionResponse>
                        ) {
                            _isLoading.value = false
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody?.status == "success") {
                                    Toast.makeText(
                                        context,
                                        "Transacción actualizada correctamente.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.popBackStack()
                                } else {
                                    _errorMessage.value = responseBody?.message ?: "Error al actualizar la transacción."
                                    Log.e("EditTransactionViewModel", "Error: ${responseBody?.message}")
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                errorBody?.let {
                                    try {
                                        val errorJson = JSONObject(it)
                                        val errorMsg = if (errorJson.has("message")) {
                                            errorJson.getString("message")
                                        } else {
                                            "Error desconocido al actualizar la transacción."
                                        }
                                        _errorMessage.value = errorMsg
                                        Log.e("EditTransactionViewModel", errorMsg)
                                    } catch (e: Exception) {
                                        _errorMessage.value = "Error al procesar la respuesta del servidor."
                                        Log.e("EditTransactionViewModel", "JSON parsing error", e)
                                    }
                                } ?: run {
                                    _errorMessage.value = "Error al actualizar la transacción: respuesta vacía del servidor."
                                    Log.e("EditTransactionViewModel", _errorMessage.value)
                                }
                            }
                        }

                        override fun onFailure(call: Call<EditTransactionResponse>, t: Throwable) {
                            _isLoading.value = false
                            _errorMessage.value = "Error de conexión al actualizar la transacción."
                            Log.e("EditTransactionViewModel", "API call failed: ${t.message}", t)
                        }
                    })
                } catch (e: Exception) {
                    _isLoading.value = false
                    _errorMessage.value = "Error al actualizar la transacción: ${e.message}"
                    Log.e("EditTransactionViewModel", "Error: ${e.message}", e)
                }
            }
        }
    }

    fun deleteTransaction(transactionId: Int, context: Context, navController: NavController) {
        _isLoading.value = true
        _errorMessage.value = ""

        // Obtén el token de sesión de SharedPreferences
        val sessionToken = SharedPreferencesHelper(context).getSessionToken() ?: run {
            _errorMessage.value = "No se encontró el token de sesión."
            Toast.makeText(
                context,
                "Error: No se encontró el token de sesión. Por favor, inicia sesión nuevamente.",
                Toast.LENGTH_LONG
            ).show()
            _isLoading.value = false
            return
        }

        // Crea el cuerpo de la solicitud
        val request = TransactionDeleteRequest(transaction_id = transactionId)

        // Realiza la llamada a la API
        TransactionInstance.api.deleteTransaction(sessionToken, request)
            .enqueue(object : Callback<TransactionDeleteResponse> {
                override fun onResponse(
                    call: Call<TransactionDeleteResponse>,
                    response: Response<TransactionDeleteResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(
                            context,
                            "Transacción eliminada correctamente.",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.popBackStack() // Regresa a la pantalla anterior
                    } else {
                        _errorMessage.value =
                            response.body()?.message ?: "Error al eliminar la transacción."
                        Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<TransactionDeleteResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión al eliminar la transacción."
                    Toast.makeText(context, _errorMessage.value, Toast.LENGTH_LONG).show()
                }
            })
    }
}