package com.example.coffetech.Auth

import com.example.coffetech.viewmodel.auth.ChangePasswordViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ChangePasswordViewModelTest {

    private lateinit var viewModel: ChangePasswordViewModel

    @Before
    fun setup() {
        viewModel = ChangePasswordViewModel()
    }

    @Test
    fun `password validation fails when passwords do not match`() {
        viewModel.onCurrentPasswordChange("OldPass123!")
        viewModel.onNewPasswordChange("NewPass123!")
        viewModel.onConfirmPasswordChange("DifferentPass123!")

        val result = viewModel.validatePasswordRequirements()
        assertFalse(result)
        assertEquals("Las contraseñas no coinciden", viewModel.errorMessage.value)
    }

    @Test
    fun `password validation succeeds with valid input`() {
        viewModel.onCurrentPasswordChange("OldPass123!")
        viewModel.onNewPasswordChange("NewPass456@")
        viewModel.onConfirmPasswordChange("NewPass456@")

        val result = viewModel.validatePasswordRequirements()
        assertTrue(result)
        assertEquals("", viewModel.errorMessage.value)
    }
}
