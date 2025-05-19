package com.example.coffetech.Auth

import com.example.coffetech.viewmodel.auth.ConfirmTokenForgotPasswordViewModel

import android.content.Context
import androidx.navigation.NavController
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ConfirmTokenForgotPasswordViewModelTest {

    private lateinit var viewModel: ConfirmTokenForgotPasswordViewModel
    private lateinit var context: Context
    private lateinit var navController: NavController

    @Before
    fun setup() {
        viewModel = ConfirmTokenForgotPasswordViewModel()
        context = mock()
        navController = mock()
    }

    @Test
    fun `onTokenChange updates token and clears error if not blank`() {
        viewModel.errorMessage.value = "Some error"
        viewModel.onTokenChange("123456")

        assertEquals("123456", viewModel.token.value)
        assertEquals("", viewModel.errorMessage.value)
    }

    @Test
    fun `onTokenChange does not clear error if token is blank`() {
        viewModel.errorMessage.value = "Error existente"
        viewModel.onTokenChange("")

        assertEquals("", viewModel.token.value)
        assertEquals("Error existente", viewModel.errorMessage.value)
    }

    @Test
    fun `confirmToken sets error when token is blank`() {
        viewModel.onTokenChange("")  // Ensure token is blank
        viewModel.confirmToken(navController, context)

        assertEquals("El token es obligatorio", viewModel.errorMessage.value)
    }

    // Nota: No se prueba el performConfirmToken aquí porque es una función externa con llamadas de red
}
