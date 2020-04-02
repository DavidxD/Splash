package com.puddlealley.flux

import android.content.Context
import com.puddlealley.flux.service.Server
import com.puddlealley.flux.service.ApiRequests
import com.puddlealley.flux.store.AppStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Create the di dependency tree for this app using koin
 */
fun createKoin(context: Context) = startKoin {
    modules(
        module {
            androidContext(context)
            single { AppStore(get()) }
            single { Server() }
            factory { ApiRequests(get()) }
        }
    )
}