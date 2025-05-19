package com.example.coffetech.Auth


import android.content.Context
import androidx.navigation.NavController
import com.example.coffetech.routes.Routes
import com.example.coffetech.viewmodel.auth.RegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var mockNavController: NavController
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        viewModel = RegisterViewModel()
        mockNavController = mock()
        mockContext = mock()
    }

    @Test
    fun `onNameChange updates name state`() {
        viewModel.onNameChange("Ana")
        assertEquals("Ana", viewModel.name.value)
    }

    @Test
    fun `onEmailChange updates email state`() {
        viewModel.onEmailChange("ana@example.com")
        assertEquals("ana@example.com", viewModel.email.value)
    }

    @Test
    fun `nextButton sets error for invalid email`() {
        viewModel.onNameChange("Ana")
        viewModel.onEmailChange("invalid-email")
        viewModel.nextButton(mockNavController, mockContext)

        assertEquals("Correo electrónico no válido", viewModel.errorMessage.value)
        verify(mockNavController, never()).navigate(any<String>(), anyOrNull(), anyOrNull())
    }

    @Test
    fun `nextButton navigates with valid email`() {
        viewModel.onNameChange("Ana")
        viewModel.onEmailChange("ana@example.com")
        viewModel.nextButton(mockNavController, mockContext)

        assertEquals("", viewModel.errorMessage.value)
        verify(mockNavController).navigate(eq("${Routes.RegisterPasswordView}/Ana/ana@example.com"), anyOrNull(), anyOrNull())
    }
}
