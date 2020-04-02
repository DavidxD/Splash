package com.puddlealley.flux.store

import com.puddlealley.flux.service.ApiRequests
import com.puddlealley.flux.store.device.SecretCaveState
import com.puddlealley.flux.store.device.secretCarvEpic
import com.puddlealley.flux.store.device.secretCaveReducer
import com.puddlealley.flux.store.login.LoginState
import com.puddlealley.flux.store.login.loginEpics
import com.puddlealley.flux.store.login.loginReducer
import com.puddlealley.splash.core.*

/**
 * A store that holds and updates the application state.
 */
class AppStore(apiRequests: ApiRequests) : DiStore<AppState>(
    appReducer,
    combineEpics(
        loginEpics(apiRequests),
        secretCarvEpic(apiRequests)
    ).toMiddleware(),
    AppState()
)

/**
 * The state that represents all data in the application.
 */
data class AppState(
    val loginState: LoginState = LoginState(),
    val secretCaveState: SecretCaveState = SecretCaveState()
) : State

/**
 * An app reducer updates the app state based on dispatched actions.
 */
val appReducer = createReducer<AppState> { action ->
    copy(
        loginState = loginReducer(loginState, action),
        secretCaveState = secretCaveReducer(secretCaveState, action)
    )
}