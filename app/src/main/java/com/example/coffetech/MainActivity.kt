// MainActivity.kt
package com.example.coffetech

import CommonDataViewModel
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.coffetech.navigation.AppNavHost
import com.example.coffetech.ui.theme.CoffeTechTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    // Obtener la instancia del ViewModel usando la propiedad delegada by viewModels()
    private val commonDataViewModel: CommonDataViewModel by viewModels()

    // Solicitud permisos de notificación
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Permiso de notificaciones concedido")
            } else {
                Log.d("MainActivity", "Permiso de notificaciones denegado")
            }
        }

    // Solicitud de permisos de localización
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Permiso de localización concedido")
            } else {
                Log.d("MainActivity", "Permiso de localización denegado")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Llamar a la función que verifica la versión y actualiza los datos si es necesario
        commonDataViewModel.updateDataIfVersionChanged(this)

        requestPermissionsIfNeeded()
        // Solicitar permiso para notificaciones si es necesario (Android 13 o superior)
        requestNotificationPermissionIfNeeded()

        // Obtener el token de FCM
        getFCMToken()

        // Establecer el contenido de la interfaz de usuario utilizando Compose
        setContent {
            CoffeTechTheme {
                AppNavHost(context = this)
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        // Solicitar permiso de notificaciones si es necesario
        requestNotificationPermissionIfNeeded()

        // Solicitar permiso de localización si es necesario
        if (androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33)
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                // Si el permiso no ha sido otorgado, lo solicitamos.
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getFCMToken() {
        // Obtener el token de FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Obtener el token de registro
            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")

            // Aquí puedes enviar el token a tu servidor backend si es necesario
        }
    }
}
