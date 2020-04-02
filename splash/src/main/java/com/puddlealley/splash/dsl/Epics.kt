package com.puddlealley.splash.dsl

import com.puddlealley.splash.core.*
import io.reactivex.Observable


/**
 * Use the builder pattern to create an epic
 */
fun <S : State> buildEpics(builder: ActionBuilder<S>.() -> Unit): Epic<S> {
    val actionBuilder = ActionBuilder<S>()
    builder(actionBuilder)
    return actionBuilder.build()
}

/**
 * attach an epic to a store.
 */
fun <S : State> StoreBuilder<S>.epics(builder: ActionBuilder<S>.() -> Unit) {
    val actionBuilder = ActionBuilder<S>()
    builder(actionBuilder)
    val build = actionBuilder.build()
    middleware(createEpicMiddleware(build))
}

/**
 * Modify the observable returned by an epic
 */
fun <S : State> Epic<S>.transform(transform: Observable<Action>.() -> Observable<Action>): Epic<S> =
        { (actions, state) ->
            this(EpicParams(transform(actions), state))
        }

@DslMarker
annotation class ActionBuilderMarker

@ActionBuilderMarker
class ActionBuilder<S : State> internal constructor() {

    private var actionCreators: List<Epic<S>> = emptyList()

    fun epic(action: Epic<S>) {
        actionCreators = actionCreators.plus(action)
    }

    inline fun <reified T : State> subEpic(crossinline epic: Epic<T>, crossinline map: (S) -> T) {
        epic(createEpic { (actions, state) ->
            epic(EpicParams(actions, state.map { map(it) }.distinctUntilChanged()))
        })
    }

    fun build(): Epic<S> = combineEpics(*actionCreators.toTypedArray())

}



