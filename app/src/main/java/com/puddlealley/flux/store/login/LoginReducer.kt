package com.puddlealley.flux.store.login

import com.puddlealley.flux.service.LoginResult
import com.puddlealley.flux.validate.EmailValidResult
import com.puddlealley.flux.validate.PasswordValidResult
import com.puddlealley.flux.validate.isEmailValid
import com.puddlealley.flux.validate.isValidPassword
import com.puddlealley.splash.core.Action
import com.puddlealley.splash.core.createReducer

val loginReducer = createReducer<LoginState> { result: Action ->
    copy(
        loading = reduceLoading(loading, result),
        email = reduceEmail(email, result),
        password = reducePassword(password, result),
        passwordError = reducePasswordError(passwordError, result),
        emailError = reduceEmailError(emailError, result)
    )
}

fun reduceEmailError(emailError: String, result: Action): String =
    when (result) {
        is LoginEvents.EmailChanged ->
            when (result.email.isEmailValid()) {
                is EmailValidResult.Valid -> ""
                is EmailValidResult.TooShort -> "Email Too short :("
                is EmailValidResult.BadlyFormatted -> "Email badly formatted :("
            }
        else -> emailError
    }

fun reducePasswordError(passwordError: String, result: Action): String =
    when (result) {
        is LoginEvents.PasswordChanged ->
            when (result.password.isValidPassword()) {
                PasswordValidResult.Valid -> ""
                PasswordValidResult.TooShort -> "Password too short :("
            }
        else -> passwordError
    }

fun reducePassword(password: String, result: Action): String =
    when (result) {
        is LoginEvents.PasswordChanged -> result.password
        else -> password
    }

fun reduceEmail(email: String, result: Action): String =
    when (result) {
        is LoginEvents.EmailChanged -> result.email
        else -> email
    }

fun reduceLoading(loading: Boolean, result: Action): Boolean =
    when (result) {
        LoginResult.Loading -> true
        is LoginResult.Error -> false
        is LoginResult.Success -> false
        else -> loading
    }
