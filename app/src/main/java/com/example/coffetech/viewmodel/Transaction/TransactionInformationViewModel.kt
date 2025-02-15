// TransactionInformationViewModel.kt
package com.example.coffetech.viewmodel.Transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.coffetech.model.ListTransactionsResponse
import com.example.coffetech.model.RetrofitInstance
import com.example.coffetech.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionInformationViewModel : ViewModel() {

    // Estado completo de transacciones
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    // Estado filtrado y ordenado de transacciones
    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    // Estados de UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    // Estados para filtros
    private val _selectedTypeFilter = MutableStateFlow("Todos")
    val selectedTypeFilter: StateFlow<String> = _selectedTypeFilter.asStateFlow()

    private val _selectedOrderFilter = MutableStateFlow("Más reciente")
    val selectedOrderFilter: StateFlow<String> = _selectedOrderFilter.asStateFlow()

    private val _totalIncomes = MutableStateFlow(0.0)
    val totalIncomes: StateFlow<Double> = _totalIncomes.asStateFlow()

    private val _totalExpenses = MutableStateFlow(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()


    /**
     * Carga las transacciones desde la API.
     */
    fun loadTransactions(plotId: Int, sessionToken: String) {
        _isLoading.value = true
        RetrofitInstance.api.listTransactions(plotId, sessionToken)
            .enqueue(object : Callback<ListTransactionsResponse> {
                override fun onResponse(
                    call: Call<ListTransactionsResponse>,
                    response: Response<ListTransactionsResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.status == "success") {
                            responseBody.data?.transactions?.let { transactionsList ->
                                _allTransactions.value = transactionsList
                                applyFiltersAndSorting()
                            }
                        } else {
                            val errorMsg = responseBody?.message ?: "Error desconocido al obtener las transacciones."
                            _errorMessage.value = errorMsg
                            Log.e("TransactionViewModel", errorMsg)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            try {
                                val errorJson = JSONObject(it)
                                val errorMsg = if (errorJson.has("message")) {
                                    errorJson.getString("message")
                                } else {
                                    "Error desconocido al obtener las transacciones."
                                }
                                _errorMessage.value = errorMsg
                                Log.e("TransactionViewModel", errorMsg)
                            } catch (e: Exception) {
                                _errorMessage.value = "Error al procesar la respuesta del servidor."
                                Log.e("TransactionViewModel", "JSON parsing error", e)
                            }
                        } ?: run {
                            _errorMessage.value = "Error al cargar las transacciones: respuesta vacía del servidor."
                            Log.e("TransactionViewModel", _errorMessage.value)
                        }
                    }
                }

                override fun onFailure(call: Call<ListTransactionsResponse>, t: Throwable) {
                    _isLoading.value = false
                    _errorMessage.value = "Error de conexión al obtener las transacciones."
                    Log.e("TransactionViewModel", "API call failed: ${t.message}", t)
                }
            })
    }

    /**
     * Actualiza el filtro de tipo de transacción.
     */
    fun updateTypeFilter(newFilter: String) {
        _selectedTypeFilter.value = newFilter
        applyFiltersAndSorting()
    }

    /**
     * Actualiza el orden de las transacciones.
     */
    fun updateOrderFilter(newOrder: String) {
        _selectedOrderFilter.value = newOrder
        applyFiltersAndSorting()
    }

    /**
     * Aplica los filtros y el ordenamiento a la lista de transacciones.
     */
    private fun applyFiltersAndSorting() {
        var tempList = _allTransactions.value

        // Aplicar filtro por tipo
        if (_selectedTypeFilter.value != "Todos") {
            tempList = tempList.filter { it.transaction_type_name == _selectedTypeFilter.value }
        }

        // Aplicar ordenamiento
        tempList = when (_selectedOrderFilter.value) {
            "Más antiguo" -> tempList.sortedBy { it.transaction_date }
            "Más reciente" -> tempList.sortedByDescending { it.transaction_date }
            else -> tempList
        }

        _filteredTransactions.value = tempList

        // Calcular totales de ingresos y gastos basados en las transacciones filtradas
        val incomes = tempList.filter { it.transaction_type_name == "Ingreso" }
        val totalIncomesSum = incomes.sumOf { it.value.toDouble() }

        val expenses = tempList.filter { it.transaction_type_name == "Gasto" }
        val totalExpensesSum = expenses.sumOf { it.value.toDouble() }

        _totalIncomes.value = totalIncomesSum
        _totalExpenses.value = totalExpensesSum
    }
}

