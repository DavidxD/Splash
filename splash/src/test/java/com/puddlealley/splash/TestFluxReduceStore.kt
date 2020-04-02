package com.puddlealley.splash

import android.R.attr
import io.kotlintest.TestCase
import io.kotlintest.matchers.equality.shouldBeEqualToUsingFields
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify

object Foo : Payload
object Bar : Payload
object Foobar : Payload
object Boom : Payload

data class ReduceState(val payload: Payload? = null) : State

class FooStore(
    initialState: ReduceState,
    dispatcher: Dispatcher
) : FluxReduceStore<ReduceState>(initialState, dispatcher) {

    override fun reduce(state: ReduceState, action: Payload): ReduceState {
        return when (action) {
            is Foo -> state.copy(payload = action)
            is Bar  -> state.copy(payload = action)
            is Foobar  -> state.copy(payload = action)
            is Boom  -> ReduceState()
            else -> state
        }
    }
}


class FluxReduceStoreTest : DescribeSpec() {

    var dispatcher = Dispatcher()
    var callback : StoreCallback = mockk(relaxed = true)
    var store = FooStore(ReduceState(), dispatcher)

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        dispatcher = Dispatcher()
        callback = mockk(relaxed = true)
        store = FooStore(ReduceState(), dispatcher)
        store.addListener(callback)
    }

    init {

        describe("FluxStore") {
            it("should respond to actions") {

                store.state.payload.shouldBe(null)

                dispatcher.dispatch(Foo)
                store.state.payload.shouldBeTypeOf<Foo>()
            }

            it("should only emit one change for multiple cache changes") {
                dispatcher.dispatch(Foo)
                dispatcher.dispatch(Foo)
                verify(exactly = 1) { callback.invoke() }
                store.state.payload.shouldBeTypeOf<Foo>()

                dispatcher.dispatch(Foobar)
                verify(exactly = 2) { callback.invoke() }
            }

            it("should not emit for empty changes") {
                dispatcher.dispatch(Foo)
                dispatcher.dispatch(Foo)

                store.state.payload.shouldBeTypeOf<Foo>()

                verify(exactly = 1) { callback.invoke() }
            }

            it("should clear the cache") {
                dispatcher.dispatch(Foo)
                verify(exactly = 1) { callback.invoke() }
                store.state.payload.shouldBeTypeOf<Foo>()

                dispatcher.dispatch(Boom)

                verify(exactly = 2) { callback.invoke() }
                store.state.payload.shouldBe(null)
            }
        }
    }

}