package com.example.coffetech.Auth


import android.content.Context
import androidx.navigation.NavController
import com.example.coffetech.viewmodel.auth.VerifyAccountViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class VerifyAccountViewModelTest {

    private lateinit var viewModel: VerifyAccountViewModel
    private lateinit var context: Context
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        viewModel = VerifyAccountViewModel()
        context = mock()
        navController = mock()
    }

    @Test
    fun `onTokenChange updates token and clears error`() {
        viewModel.errorMessage.value = "algún error"

        viewModel.onTokenChange("123456")

        assertEquals("123456", viewModel.token.value)
        assertEquals("", viewModel.errorMessage.value)
    }

    @Test
    fun `onTokenChange does not clear error if token is blank`() {
        viewModel.errorMessage.value = "token requerido"

        viewModel.onTokenChange("") // still blank

        assertEquals("", viewModel.token.value)
        assertEquals("token requerido", viewModel.errorMessage.value)
    }

    @Test
    fun `verifyUser sets error if token is blank`() {
        viewModel.onTokenChange("") // aseguramos que esté en blanco

        viewModel.verifyUser(navController, context)

        assertEquals("El token es obligatorio", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }
}
