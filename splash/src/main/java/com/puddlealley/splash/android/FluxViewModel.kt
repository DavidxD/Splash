package com.puddlealley.splash.android

import com.puddlealley.splash.core.State
import com.puddlealley.splash.core.Store

abstract class FluxViewModel<VS : State>(private val store: Store<VS>) : AutoDisposeViewModel(), Store<VS> by store {

    init {
        store.updates.connect(this)
    }
}
