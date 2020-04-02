package com.puddlealley.flux.store.device

import com.puddlealley.flux.service.CodeVerificationResult
import com.puddlealley.splash.core.Action
import com.puddlealley.splash.core.createReducer
import timber.log.Timber

val secretCaveReducer = createReducer<SecretCaveState> { action ->
    copy(
        loading = reducerLoading(loading, action),
        codeCorrect = reduceCodeCorrect(codeCorrect, action),
        enteredCode = reduceEnteredCode(enteredCode, action)
    )
}


fun reduceEnteredCode(enteredCode: String, action: Action): String =
    when (action) {
        is SecretCaveEvents.LetteredEntered -> enteredCode + action.letter
        is SecretCaveEvents.ClearCodeClicked -> SecretCaveEvents.EMPTY_CODE
        is CodeVerificationResult.Error -> SecretCaveEvents.EMPTY_CODE //DRB: if there is an error then we remove the password
        is CodeVerificationResult.Success -> SecretCaveEvents.EMPTY_CODE //DRB: if the password is not correct then we remove it
        else -> enteredCode
    }


fun reducerLoading(loading: Boolean, action: Action): Boolean =
    when (action) {
        CodeVerificationResult.Loading -> true
        is CodeVerificationResult.Error -> false
        is CodeVerificationResult.Success -> false
        else -> loading
    }


fun reduceCodeCorrect(codeCorrect: Boolean, action: Action): Boolean =
    when (action) {
        is CodeVerificationResult.Success -> action.correct
        is CodeVerificationResult.Error -> false
        else -> codeCorrect
    }