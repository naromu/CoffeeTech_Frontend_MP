package com.example.coffetech.Auth

import android.content.Context
import androidx.navigation.NavController
import com.example.coffetech.viewmodel.auth.LoginViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var mockContext: Context
    private lateinit var mockNavController: NavController

    @Before
    fun setup() {
        viewModel = LoginViewModel()
        mockContext = mock()
        mockNavController = mock()
    }
    @Test
    fun `should not allow login when fields are empty`() {
        val result = viewModel.shouldAllowLogin()
        assertFalse(result)
        assertEquals("El correo y la contraseña son obligatorios", viewModel.errorMessage.value)
    }

    @Test
    fun `should not allow login with invalid email`() {
        viewModel.onEmailChange("bad-email")
        viewModel.onPasswordChange("password123")
        val result = viewModel.shouldAllowLogin()
        assertFalse(result)
        assertEquals("Correo electrónico no válido", viewModel.errorMessage.value)
    }

}
