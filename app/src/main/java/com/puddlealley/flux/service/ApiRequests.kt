package com.puddlealley.flux.service

import com.puddlealley.splash.core.Result
import io.reactivex.Observable
import timber.log.Timber

sealed class LoginResult : Result {
    object Loading : LoginResult()
    data class Error(val error: String) : LoginResult()
    data class Success(val user: User) : LoginResult()
}

sealed class CodeVerificationResult : Result {
    object Loading : CodeVerificationResult()
    data class Error(val error: String) : CodeVerificationResult()
    data class Success(val correct: Boolean)  : CodeVerificationResult()
}

class ApiRequests(private val server: Server) {

    fun login(email :String, password: String): Observable<LoginResult> =
        server.login(email, password)
            .map<LoginResult> {
                LoginResult.Success(it)
            }.onErrorReturn {
                Timber.e(it,"error loading User")
                LoginResult.Error(it.message.orEmpty())
            }
            .toObservable()
            .startWith(LoginResult.Loading)

    /**
     * Verifies the secret code.
     */
    fun codeVerification(secretCode: String) : Observable<CodeVerificationResult> =
        server.validateSecretCode(secretCode)
            .map<CodeVerificationResult> {
                CodeVerificationResult.Success(it)
            }.onErrorReturn {
                Timber.e(it,"error verifying code")
                CodeVerificationResult.Error(it.message.orEmpty())
            }
            .toObservable()
            .startWith(CodeVerificationResult.Loading)
}