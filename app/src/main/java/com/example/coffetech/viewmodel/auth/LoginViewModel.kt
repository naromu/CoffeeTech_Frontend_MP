package com.example.coffetech.viewmodel.auth

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.coffetech.model.AuthRetrofitInstance
import com.example.coffetech.model.LoginRequest
import com.example.coffetech.model.LoginResponse
import com.example.coffetech.routes.Routes
import com.example.coffetech.utils.SharedPreferencesHelper
import com.example.coffetech.viewmodel.auth.others.performLogin
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel for managing the state and logic of the login flow.
 * This ViewModel handles user input validation, performing the login request, and navigation.
 */
class LoginViewModel() : ViewModel(), Parcelable {

    // State variables for managing email, password, error messages, and loading status
    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf("")
        private set

    var isLoading = mutableStateOf(false)
        private set

    /**
     * Parcelable constructor for restoring the ViewModel state if needed.
     */
    constructor(parcel: Parcel) : this()

    /**
     * Updates the email value when the user inputs a new email.
     *
     * @param newEmail The new email entered by the user.
     */
    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    /**
     * Updates the password value when the user inputs a new password.
     *
     * @param newPassword The new password entered by the user.
     */
    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginViewModel> {
        override fun createFromParcel(parcel: Parcel): LoginViewModel {
            return LoginViewModel(parcel)
        }

        override fun newArray(size: Int): Array<LoginViewModel?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * Performs the login process by sending a login request to the server.
     * It first validates the email format and ensures both email and password are not blank.
     * If the login is successful, the session token, name, and email are stored in SharedPreferences,
     * and the user is navigated to the StartView. In case of an error, it handles error messages and logs them.
     *
     * @param navController The [NavController] used for navigating between screens.
     * @param context The [Context] used to show Toast messages and access SharedPreferences.
     */
    fun loginUser(navController: NavController, context: Context) {
        if (!shouldAllowLogin()) return

        isLoading.value = true

        performLogin(
            email = email.value,
            password = password.value,
            context = context,
            navController = navController,
            onLoading = { isLoading.value = it },
            onError = { errorMessage.value = it }
        )
    }



    /**
     * Validates the email format using Android's email address pattern matcher.
     *
     * @param email The email address to validate.
     * @return `true` if the email format is valid, `false` otherwise.
     */
    private fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }


    fun shouldAllowLogin(): Boolean {
        return when {
            email.value.isBlank() || password.value.isBlank() -> {
                errorMessage.value = "El correo y la contraseña son obligatorios"
                false
            }
            !validateEmail(email.value) -> {
                errorMessage.value = "Correo electrónico no válido"
                false
            }
            else -> {
                errorMessage.value = ""
                true
            }
        }
    }


}

