package com.puddlealley.flux.store.login

import com.puddlealley.flux.service.ApiRequests
import com.puddlealley.flux.store.AppState
import com.puddlealley.splash.core.createEpic
import io.reactivex.rxkotlin.ofType

fun loginEpics(apiRequests: ApiRequests) =
    createEpic<AppState> { (events, state) ->
        val loginClicked = events.ofType<LoginEvents.LoginClicked>()
        // perform the login
        loginClicked.flatMap {
            val loginState = state.blockingFirst().loginState
            apiRequests.login(loginState.email.orEmpty(), loginState.password.orEmpty())
        }
    }