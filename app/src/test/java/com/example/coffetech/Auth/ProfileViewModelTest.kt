package com.example.coffetech.viewmodel.auth

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var context: Context

    @Before
    fun setUp() {
        viewModel = ProfileViewModel()
        context = mock()
    }

    @Test
    fun `onNameChange updates name and clears error if not blank`() {
        viewModel.nameErrorMessage.value = "algún error"
        viewModel.onNameChange("Juan")

        assertEquals("Juan", viewModel.name.value)
        assertTrue(viewModel.isProfileUpdated.value)
        assertEquals("", viewModel.nameErrorMessage.value)
    }

    @Test
    fun `onNameChange keeps error if name is blank`() {
        viewModel.nameErrorMessage.value = "Error previo"
        viewModel.onNameChange("") // blank

        assertEquals("", viewModel.name.value)
        assertTrue(viewModel.isProfileUpdated.value)
        assertEquals("Error previo", viewModel.nameErrorMessage.value)
    }




}
