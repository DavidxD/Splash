package com.puddlealley.flux.store.device

import com.puddlealley.splash.core.Event
import com.puddlealley.splash.core.State


data class SecretCaveState(
    val loading : Boolean = false,
    val codeCorrect : Boolean = false,
    val enteredCode: String = SecretCaveEvents.EMPTY_CODE
): State

sealed class SecretCaveEvents : Event {
    data class LetteredEntered(val letter: String) : SecretCaveEvents()
    data class CodeEntered(val code: String) : SecretCaveEvents()
    object ClearCodeClicked : SecretCaveEvents()

    companion object{

        const val EMPTY_CODE = ""

    }
}

