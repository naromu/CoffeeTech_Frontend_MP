package com.example.coffetech.view.Transaction

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffetech.R
import com.example.coffetech.Routes.Routes
import com.example.coffetech.common.*
import com.example.coffetech.model.Flowering
import com.example.coffetech.model.Transaction
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.view.common.HeaderFooterSubView
import com.example.coffetech.viewmodel.Transaction.TransactionInformationViewModel
import com.example.coffetech.viewmodel.farm.FarmInformationViewModel
import com.example.coffetech.viewmodel.flowering.FloweringInformationViewModel
import kotlinx.coroutines.flow.map
import java.net.URLEncoder

@Composable
fun TransactionInformationView(
    navController: NavController,
    plotId: Int,
    plotName: String,
    farmName: String,
    farmId: Int,
    viewModel: TransactionInformationViewModel = viewModel() // Inyecta el ViewModel aquí
) {
    // Obtener el contexto para acceder a SharedPreferences o cualquier otra fuente del sessionToken
    val context = LocalContext.current
    val sessionToken = remember { SharedPreferencesHelper(context).getSessionToken() }
    Log.d("TransactionView", "Recibiendo datos : /$plotId/$plotName/$farmName/$farmId")
    val totalIncomes by viewModel.totalIncomes.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()

    // Llamar a loadTransactions cuando la vista se cargue
    LaunchedEffect(plotId) {
        sessionToken?.let {
            viewModel.loadTransactions(plotId, it)
        } ?: run {
            viewModel.apply {
                // Simulamos un error ya que no hay token
                // Puedes adaptar esto según tus necesidades
                // Por ejemplo, navegar a la pantalla de inicio de sesión
                // o mostrar un mensaje de error
            }
        }
    }

    // Obtener los estados del ViewModel
    val transactions by viewModel.filteredTransactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsState()
    val selectedOrderFilter by viewModel.selectedOrderFilter.collectAsState()
    val balance = totalIncomes - totalExpenses

// Puedes formatear los valores según tus necesidades
    val formattedTotalIncomes = String.format("%,.2f", totalIncomes)
    val formattedTotalExpenses = String.format("%,.2f", totalExpenses)
    val formattedBalance = String.format("%,.2f", balance)

    // Acción para editar una transacción
    val onEditClick: (Transaction) -> Unit = { transaction ->
        Log.d("TransactionView", "Editar transacción con ID: ${transaction.transaction_id}")

        // Verificar y reemplazar la descripción vacía con un placeholder
        val descriptionToPass = if (transaction.description.isNullOrEmpty()) "NoDescription" else transaction.description
        val encodedDescription = URLEncoder.encode(descriptionToPass, "UTF-8")
        val encodedTransactionTypeName = URLEncoder.encode(transaction.transaction_type_name, "UTF-8")
        val encodedTransactionCategoryName = URLEncoder.encode(transaction.transaction_category_name, "UTF-8")
        val encodedTransactionDate = URLEncoder.encode(transaction.transaction_date, "UTF-8")

        // Construir la ruta con los parámetros codificados
        val route = "${Routes.EditTransactionView}/${transaction.transaction_id}/$encodedTransactionTypeName/$encodedTransactionCategoryName/$encodedDescription/${transaction.value}/$encodedTransactionDate"
        navController.navigate(route)
    }

    // Vista principal
    HeaderFooterSubView(
        title = "Transacciones",
        currentView = "Fincas",
        navController = navController,
        onBackClick = { navController.navigate("${Routes.PlotInformationView}/$plotId/$farmName/$farmId") },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
        ) {
            // Contenido desplazable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Hacer la columna scrolleable verticalmente
            ) {
                if (isLoading) {
                    // Mostrar un indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cargando Transacciones...",
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (errorMessage.isNotEmpty()) {
                    // Mostrar el error si ocurrió algún problema
                    Text(text = errorMessage, color = Color.Red)
                } else {
                    // Mostrar el nombre del lote
                    Text(text = "Lote: ${plotName.ifEmpty { "Sin Nombre de lote" }}", color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros de Transacciones
                    TransactionFilterDropdowns(
                        selectedTypeFilter = selectedTypeFilter,
                        onTypeFilterChange = { newType ->
                            viewModel.updateTypeFilter(newType)
                        },
                        selectedOrderFilter = selectedOrderFilter,
                        onOrderFilterChange = { newOrder ->
                            viewModel.updateOrderFilter(newOrder)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar el saldo si es necesario
                    SaldoCard(
                        balance = formattedBalance
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de Transacciones
                    transactions.forEach { transaction ->
                        TransactionCard(
                            transactionType = transaction.transaction_type_name,
                            transactionCategoryName = transaction.transaction_category_name, // Pasar la categoría
                            amount = transaction.value.toString(),
                            description = transaction.description ?: "Sin descripción",
                            date = transaction.transaction_date,
                            onEditClick = { onEditClick(transaction) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            // Botón flotante alineado al fondo derecho
            CustomFloatingActionButton(
                onAddClick = {
                    // Navegar a la pantalla para crear una nueva transacción
                   navController.navigate("${Routes.AddTransactionView}/$plotId")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FloweringInformationViewPreview() {
    CoffeTechTheme {
        TransactionInformationView(
            navController = NavController(LocalContext.current),
            plotId = 1, // Valor simulado de plotId para la previsualización
            plotName= "",
            farmName = "Finca Ejemplo",
            farmId = 1,
        )
    }
}