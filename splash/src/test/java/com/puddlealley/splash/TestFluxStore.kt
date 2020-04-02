package com.puddlealley.splash

import io.kotlintest.TestCase
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.shouldThrow
import io.kotlintest.specs.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.lang.IllegalStateException

 class ExampleState : State
 class ChangeState : Payload
 class IgnoreState : Payload

private class TestFluxStore(dispatcher: Dispatcher) : FluxStore(dispatcher) {

    override fun onDispatch(payload: Payload) {
        when (payload) {
            is ChangeState -> emitChange()
        }
    }
}

private  class IllegalFluxStore(dispatcher: Dispatcher) : FluxStore(dispatcher) {
    override fun onDispatch(payload: Payload) {

    }

    fun illegalEmit() = emitChange()
}

class FluxStoreTest : DescribeSpec() {

    private var dispatcher = spyk<Dispatcher>()
    private var fluxStore = TestFluxStore(dispatcher)

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        dispatcher = spyk<Dispatcher>()
        fluxStore = TestFluxStore(dispatcher)
    }

    init {
        describe("FluxStore") {
            it("registers a callback with the dispatcher") {
                verify(exactly = 1) {
                    dispatcher.register(any())
                }
            }
            it("throws when __emitChange() is invoked outside of a dispatch") {
                val illegalStore = IllegalFluxStore(dispatcher)
                shouldThrow<IllegalStateException> {
                    illegalStore.illegalEmit()
                }
            }

            it("throws when hasChanged() is invoked outside of a dispatch") {
                shouldThrow<IllegalStateException> {
                    fluxStore.hasChanged()
                }
            }

            it("emits an event on state change") {
                every {  dispatcher.isDispatching() } returns true
                val callback : StoreCallback = mockk(relaxed = true)
                fluxStore.addListener(callback)

                dispatcher.dispatch(ChangeState())

                verify { callback.invoke() }
            }

            it("exposes whether the state has changed during current dispatch") {
                every {  dispatcher.isDispatching() } returns true
                dispatcher.dispatch(ChangeState())
                fluxStore.hasChanged().shouldBeTrue()
                dispatcher.dispatch(IgnoreState())
                fluxStore.hasChanged().shouldBeFalse()
            }

            it("exposes the dispatch token in a getter") {
                fluxStore.getDispatchToken().shouldNotBeNull()
            }

            it("calls two listeners when attached") {
                every {  dispatcher.isDispatching() } returns true
                val callback : StoreCallback = mockk(relaxed = true)
                val callback1 : StoreCallback = mockk(relaxed = true)
                fluxStore.addListener(callback)
                fluxStore.addListener(callback1)

                dispatcher.dispatch(ChangeState())

                verify { callback.invoke() }
                verify { callback1.invoke() }
            }

            it("removes listener") {
                every {  dispatcher.isDispatching() } returns true
                val callback : StoreCallback = mockk(relaxed = true)
                val callback1 : StoreCallback = mockk(relaxed = true)
                fluxStore.addListener(callback)()
                fluxStore.addListener(callback1)()

                dispatcher.dispatch(ChangeState())

                verify(exactly = 0) { callback.invoke() }
                verify(exactly = 0){ callback1.invoke() }
            }
            
        }
    }

}