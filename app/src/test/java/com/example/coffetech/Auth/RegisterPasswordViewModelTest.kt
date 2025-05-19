package com.example.coffetech.Auth

import com.example.coffetech.viewmodel.auth.RegisterPasswordViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterPasswordViewModelTest {

    private lateinit var viewModel: RegisterPasswordViewModel

    @Before
    fun setUp() {
        viewModel = RegisterPasswordViewModel()
    }

    @Test
    fun `password fields update correctly`() {
        viewModel.onPasswordChange("SecurePass123!")
        viewModel.onConfirmPasswordChange("SecurePass123!")

        assertEquals("SecurePass123!", viewModel.password.value)
        assertEquals("SecurePass123!", viewModel.confirmPassword.value)
    }

    @Test
    fun `validatePassword returns error when passwords do not match`() {
        val result = invokeValidatePassword("SecurePass123!", "Mismatch123!")
        assertTrue(result.contains("Las contraseñas no coinciden"))
    }

    @Test
    fun `validatePassword returns error for short password`() {
        val result = invokeValidatePassword("A1!", "A1!")
        assertTrue(result.contains("La contraseña debe tener al menos 8 caracteres"))
    }

    @Test
    fun `validatePassword returns error when missing special character`() {
        val result = invokeValidatePassword("Password123", "Password123")
        assertTrue(result.contains("La contraseña debe contener al menos un carácter especial"))
    }

    @Test
    fun `validatePassword returns error when missing uppercase letter`() {
        val result = invokeValidatePassword("password123!", "password123!")
        assertTrue(result.contains("La contraseña debe contener al menos una letra mayúscula"))
    }

    @Test
    fun `validatePassword passes with valid password`() {
        val result = invokeValidatePassword("ValidPass123!", "ValidPass123!")
        assertTrue(result.isEmpty())
    }

    // Usa reflexión para invocar el método privado de validación
    private fun invokeValidatePassword(password: String, confirmPassword: String): List<String> {
        val method = RegisterPasswordViewModel::class.java.getDeclaredMethod(
            "validatePassword", String::class.java, String::class.java
        )
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(viewModel, password, confirmPassword) as List<String>
    }
}
