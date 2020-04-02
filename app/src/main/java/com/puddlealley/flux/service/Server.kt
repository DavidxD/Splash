package com.puddlealley.flux.service

import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Represents basic calls to the server i.e a Retrofit service.
 */
class Server {

    /**
     * Mock call that loads a user
     */
    fun loadUser(userId: String): Single<User> {
        return Single.just(User(id = userId, name = "Rory")).delay(3, TimeUnit.SECONDS)
    }

    fun login(email: String, password: String): Single<User> {
        return Single.just(User(id = "userId", name = "Rory")).delay(3, TimeUnit.SECONDS)
    }

    /**
     * Mock call that validates a secret code
     */
    fun validateSecretCode(secretCode: String) : Single<Boolean> =
        Single.just(secretCode.toUpperCase(Locale.ROOT) == "ABCABC").delay(3, TimeUnit.SECONDS)

}

data class User(val id: String, val name: String)
