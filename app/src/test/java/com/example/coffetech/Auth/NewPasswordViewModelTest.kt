package com.example.coffetech.Auth

import com.example.coffetech.viewmodel.auth.NewPasswordViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NewPasswordViewModelTest {

    private lateinit var viewModel: NewPasswordViewModel

    @Before
    fun setUp() {
        viewModel = NewPasswordViewModel()
    }

    @Test
    fun `password and confirm password are updated correctly`() {
        viewModel.onPasswordChange("Secure123!")
        viewModel.onConfirmPasswordChange("Secure123!")

        assertEquals("Secure123!", viewModel.password.value)
        assertEquals("Secure123!", viewModel.confirmPassword.value)
    }

    @Test
    fun `validation fails when password and confirm password do not match`() {
        viewModel.onPasswordChange("Password123!")
        viewModel.onConfirmPasswordChange("Different123!")

        val result = invokeValidatePassword(viewModel, "Password123!", "Different123!")
        assertFalse(result.first)
        assertEquals("Las contraseñas no coinciden", result.second)
    }

    @Test
    fun `validation fails when password is too short`() {
        val result = invokeValidatePassword(viewModel, "A1!", "A1!")
        assertFalse(result.first)
        assertEquals("La contraseña debe tener al menos 8 caracteres", result.second)
    }

    @Test
    fun `validation fails when password lacks special character`() {
        val result = invokeValidatePassword(viewModel, "Password123", "Password123")
        assertFalse(result.first)
        assertEquals("La contraseña debe contener al menos un carácter especial", result.second)
    }

    @Test
    fun `validation fails when password lacks uppercase letter`() {
        val result = invokeValidatePassword(viewModel, "password123!", "password123!")
        assertFalse(result.first)
        assertEquals("La contraseña debe contener al menos una letra mayúscula", result.second)
    }

    @Test
    fun `validation passes with strong password`() {
        val result = invokeValidatePassword(viewModel, "StrongPass1!", "StrongPass1!")
        assertTrue(result.first)
        assertEquals("Contraseña válida", result.second)
    }

    // Accede al método privado usando reflexión para testearlo sin cambiar su visibilidad
    private fun invokeValidatePassword(
        viewModel: NewPasswordViewModel,
        password: String,
        confirmPassword: String
    ): Pair<Boolean, String> {
        val method = NewPasswordViewModel::class.java.getDeclaredMethod(
            "validatePassword", String::class.java, String::class.java
        )
        method.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return method.invoke(viewModel, password, confirmPassword) as Pair<Boolean, String>
    }
}
