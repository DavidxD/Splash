package com.puddlealley.splash.dsl

import com.puddlealley.splash.core.Action
import com.puddlealley.splash.core.Reducer
import com.puddlealley.splash.core.State
import com.puddlealley.splash.core.combineReducers


class ReducerBuilder<S : State> {

    private var reducers: List<Reducer<S>> = emptyList()

    fun createReducer(function: S.(action: Action) -> S) {
        this.reducers =
                reducers.plus { oldState, action ->
                    oldState.function(action)
                }
    }

    fun build(): Reducer<S> = combineReducers(reducers)
}


