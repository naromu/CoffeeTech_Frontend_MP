package com.example.coffetech.viewmodel.common

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.Routes.Routes

class HeaderFooterSubViewModel : ViewModel() {

    // Estado que controla el título de la vista
    private val _title = MutableLiveData("Mis Fincas")
    val title: LiveData<String> = _title

    // Función para actualizar el título si se desea en el futuro
    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }





    // FOOTER FUNCTIONS


    fun onHomeClick(navController: NavController) {
        navController.navigate(Routes.StartView) // Navegar a Home o la vista correspondiente
    }

    fun onFincasClick(navController: NavController) {
        navController.navigate(Routes.FarmView) // Navegar a la vista de Fincas
    }

    fun onCentralButtonClick( context: Context) {
        // Aquí podrías agregar la lógica para el botón central
        Toast.makeText(context, "Función disponible proximamente", Toast.LENGTH_SHORT).show()

    }

    fun onReportsClick(navController: NavController, context: Context) {
        Toast.makeText(context, "Función disponible proximamente", Toast.LENGTH_SHORT).show()

        //navController.navigate("reportsView") // Navegar a la vista de reportes (deberás crear esta ruta)
    }

    fun onCostsClick(navController: NavController, context: Context) {
        Toast.makeText(context, "Función disponible proximamente", Toast.LENGTH_SHORT).show()

        //navController.navigate("costsView") // Navegar a la vista de costos (deberás crear esta ruta)
    }

}
