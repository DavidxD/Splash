package com.puddlealley.flux.store.login

import com.puddlealley.flux.validate.EmailValidResult
import com.puddlealley.flux.validate.PasswordValidResult
import com.puddlealley.flux.validate.isEmailValid
import com.puddlealley.flux.validate.isValidPassword
import com.puddlealley.splash.core.Event
import com.puddlealley.splash.core.State

sealed class LoginEvents : Event {
    data class EmailChanged(val email: String) : LoginEvents()
    data class PasswordChanged(val password: String) : LoginEvents()
    object LoginClicked : LoginEvents()
}

data class LoginState(
    // Loading state.
    val loading: Boolean = false,
    // Form state.
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = ""
) : State {

    // Button state.
    val canSignIn: Boolean = emailError.isEmpty() && passwordError.isEmpty()
}