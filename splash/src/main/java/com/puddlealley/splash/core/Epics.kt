package com.puddlealley.splash.core

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.lang.IllegalStateException

/**
 * @param actions A stream of all actions dispatched via the dispatcher.
 * @param stateChanges Stream of updates to the store, emits state immediately on subscription.
*/
data class EpicParams<S>(val actions: Observable<Action>, val stateChanges: Observable<S>)
typealias Epic<S> = (EpicParams<S>) -> Observable<Action>

/**
 * Utility to create epic middleware
 */
fun <S : State> createEpicMiddleware(epic: Epic<S>): Middleware<S> = { store ->
    epic(EpicParams(store.actions, store.updates))
        .doOnNext {
            if(it is Event){
                throw IllegalStateException("epics should not emit Events, only Results.")
            }
        }
        .doOnNext { store.dispatch(it) }.ignoreElements()
}

fun <T : State> Epic<T>.toMiddleware(): Middleware<T> = createEpicMiddleware(this)

/**
 * Create an epic.
 */
fun <S : State> createEpic(epic: Epic<S>): Epic<S> = epic

fun <T : State> combineEpics(vararg epic: Epic<T>): Epic<T> =
        { payloads -> epic.toObservable().flatMap { it(payloads) } }
