package com.example.coffetech.viewmodel.auth

import android.content.Context
import androidx.navigation.NavController
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ForgotPasswordViewModelTest {

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var context: Context
    private lateinit var navController: NavController

    @Before
    fun setup() {
        viewModel = ForgotPasswordViewModel()
        context = mock()
        navController = mock()
    }

    @Test
    fun `onEmailChange sets valid email and clears error`() {
        viewModel.onEmailChange("test@example.com")
        assertTrue(viewModel.isEmailValid.value)
        assertEquals("", viewModel.errorMessage.value)
    }

    @Test
    fun `onEmailChange sets invalid email and shows error`() {
        viewModel.onEmailChange("invalid-email")
        assertFalse(viewModel.isEmailValid.value)
        assertEquals("Correo electrónico no válido", viewModel.errorMessage.value)
    }

    @Test
    fun `sendForgotPasswordRequest shows error if email invalid`() {
        viewModel.onEmailChange("bad-email")
        viewModel.sendForgotPasswordRequest(navController, context)
        assertEquals("Correo electrónico no válido", viewModel.errorMessage.value)
    }
}
