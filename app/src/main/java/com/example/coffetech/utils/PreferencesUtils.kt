package com.example.coffetech.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import com.example.coffetech.model.Role
import com.example.coffetech.model.UnitMeasure
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("myAppPreferences", Context.MODE_PRIVATE)

    // Inicializa Gson para la serialización/deserialización de objetos
    private val gson = Gson()

    // ============================= MANEJO DE VERSIONES ============================= //

    // Obtener el código de versión actual de la app
    fun getCurrentVersionCode(context: Context): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(
                "SharedPreferences",
                "No se pudo obtener el código de versión: ${e.localizedMessage}"
            )
            -1
        }
    }

    // Obtener el código de versión almacenado
    fun getSavedVersionCode(): Int {
        return sharedPref.getInt("version_code", -1)  // -1 indica que no hay versión almacenada
    }

    // Guardar el código de versión actual
    fun saveVersionCode(versionCode: Int) {
        with(sharedPref.edit()) {
            putInt("version_code", versionCode)
            apply()
        }
    }

    // ============================= MANEJO DE ROLES ============================= //

    // Guardar roles incluyendo los permisos asociados
    fun saveRoles(roles: List<Role>) {
        try {
            val jsonRoles = gson.toJson(roles)
            with(sharedPref.edit()) {
                putString("roles", jsonRoles)
                apply()
            }
        } catch (e: Exception) {
            Log.e("SharedPreferences", "Error guardando roles: ${e.localizedMessage}")
        }
    }


    // Obtener roles
    fun getRoles(): List<Role>? {
        return try {
            val jsonRoles = sharedPref.getString("roles", null) ?: return null
            val type = object : com.google.gson.reflect.TypeToken<List<Role>>() {}.type
            gson.fromJson<List<Role>>(jsonRoles, type)
        } catch (e: Exception) {
            Log.e("SharedPreferences", "Error obteniendo roles: ${e.localizedMessage}")
            null
        }
    }

    // ============================= MANEJO DE UNIDADES DE MEDIDA ============================= //

    // Guardar unidades de medida
    fun saveUnitMeasures(unitMeasures: List<UnitMeasure>) {
        try {
            val jsonUnits = gson.toJson(unitMeasures)
            with(sharedPref.edit()) {
                putString("unit_measures", jsonUnits)
                apply()
            }
        } catch (e: Exception) {
            Log.e("SharedPreferences", "Error guardando unidades de medida: ${e.localizedMessage}")
        }
    }

    // Obtener unidades de medida
    fun getUnitMeasures(): List<UnitMeasure>? {
        return try {
            val jsonUnits = sharedPref.getString("unit_measures", null) ?: return null
            val type = object : com.google.gson.reflect.TypeToken<List<UnitMeasure>>() {}.type
            gson.fromJson<List<UnitMeasure>>(jsonUnits, type)
        } catch (e: Exception) {
            Log.e("SharedPreferences", "Error obteniendo unidades de medida: ${e.localizedMessage}")
            null
        }
    }

    // ============================= OTRAS FUNCIONES ============================= //

    // Guardar estado de actualización de datos
    fun setDataUpdated(isUpdated: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("data_updated", isUpdated)
            apply()
        }
    }

    // Verificar si los datos están actualizados
    fun isDataUpdated(): Boolean {
        return sharedPref.getBoolean("data_updated", false)
    }

    // Reiniciar el estado de actualización de datos
    fun resetDataUpdatedFlag() {
        setDataUpdated(false)
    }

    // Función para guardar el token, nombre y correo
    fun saveSessionData(token: String, name: String, email: String) {
        with(sharedPref.edit()) {
            putString("session_token", token)
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }
    }

    // Función para verificar si el usuario está logueado
    fun isLoggedIn(): Boolean {
        return sharedPref.getString("session_token", null) != null
    }

    // Función para obtener el token de sesión
    fun getSessionToken(): String? {
        return sharedPref.getString("session_token", null)
    }

    // Función para obtener el nombre de usuario
    fun getUserName(): String {
        return sharedPref.getString("user_name", "Usuario") ?: "Usuario"
    }

    // Función para obtener el correo de usuario
    fun getUserEmail(): String {
        return sharedPref.getString("user_email", "") ?: ""
    }

    // Función para eliminar los datos de sesión (cerrar sesión)
    fun clearSession() {
        with(sharedPref.edit()) {
            remove("session_token")
            remove("user_name")
            remove("user_email")
            apply()
        }
    }

    fun setVerificationStatus(isVerified: Boolean) {
        with(sharedPref.edit()) {
            putBoolean("is_verified", isVerified)
            apply()
        }
    }

    fun isVerified(): Boolean {
        return sharedPref.getBoolean("is_verified", false)
    }
    // ============================= MANEJO DE VARIEDADES DE CAFÉ ============================= //

    // Función para guardar las variedades de café
    fun saveCoffeeVarieties(varieties: List<String>) {
        val editor = sharedPref.edit()
        editor.putStringSet("coffee_varieties", varieties.toSet())
        editor.apply()
    }

    // Función para obtener las variedades de café
    fun getCoffeeVarieties(): List<String>? {
        return sharedPref.getStringSet("coffee_varieties", null)?.toList()
    }
}
