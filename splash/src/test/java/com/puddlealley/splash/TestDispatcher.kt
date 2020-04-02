package com.puddlealley.splash

import com.puddlealley.splash.Dispatcher
import com.puddlealley.splash.Payload
import com.puddlealley.splash.PayloadCallback
import io.kotlintest.TestCase
import io.kotlintest.shouldThrow
import io.kotlintest.specs.DescribeSpec
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.*

class TestDispatcher : DescribeSpec() {

    private var dispatcher = Dispatcher()
    private var callbackA = mockk<PayloadCallback>(relaxed = true)
    private var callbackB = mockk<PayloadCallback>(relaxed = true)

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)
        dispatcher = Dispatcher()
        callbackA = mockk<PayloadCallback>(relaxed = true)
        callbackB = mockk<PayloadCallback>(relaxed = true)
    }

    init {
        describe("FluxStore") {

            it("should execute all subscriber callbacks"){
                dispatcher.register(callbackA)
                dispatcher.register(callbackB)

                class DemoPayload : Payload

                val demoPayload = DemoPayload()
                dispatcher.dispatch(demoPayload)

                verify(exactly = 1) { callbackA.invoke(demoPayload) }
                verify(exactly = 1) { callbackB.invoke(demoPayload) }


                dispatcher.dispatch(demoPayload)

                verify(exactly = 2) { callbackA.invoke(demoPayload) }
                verify(exactly = 2) { callbackB.invoke(demoPayload) }
            }

            it("should wait for callbacks registered earlier") {
                val tokenA = dispatcher.register(callbackA)
                val demoPayload = object : Payload {}

                dispatcher.register { payload ->
                    dispatcher.waitFor(tokenA)
                    verify(exactly = 1) { callbackA.invoke(demoPayload) }
                    callbackB(payload)
                }

                dispatcher.dispatch(demoPayload)

                verify(exactly = 1) { callbackA.invoke(demoPayload) }
                verify(exactly = 1) { callbackB.invoke(demoPayload) }
            }

            it("should wait for callbacks registered later") {
                var tokenB : UUID? = null
                val demoPayload = object : Payload {}
                dispatcher.register{ payload ->
                    dispatcher.waitFor(tokenB!!)

                    verify(exactly = 1) { callbackB.invoke(demoPayload) }
                    callbackA(payload)
                }

                tokenB = dispatcher.register(callbackB)
                dispatcher.dispatch(demoPayload)
                
                verify(exactly = 1) { callbackA.invoke(demoPayload) }
                verify(exactly = 1) { callbackB.invoke(demoPayload) }
            }

            it("should throw if dispatch() while dispatching") {
                val demoPayload = object : Payload {}
                dispatcher.register{ payload ->
                    dispatcher.dispatch(payload)
                    callbackA(demoPayload)
                }

                shouldThrow<IllegalStateException> {
                    dispatcher.dispatch(demoPayload)
                }
                verify(exactly = 0) { callbackA.invoke(demoPayload) }
            }

            it("should throw if waitFor() while not dispatching") {
                val tokenA = dispatcher.register(callbackA)

                shouldThrow<IllegalStateException> {
                    dispatcher.waitFor(tokenA)
                }
                verify(exactly = 0) { callbackA.invoke(any()) }
            }

            it("should throw if waitFor() with invalid token"){
                var invalidToken = UUID.randomUUID()
                val demoPayload = object : Payload {}

                dispatcher.register{ dispatcher.waitFor(invalidToken) }

                shouldThrow<IllegalStateException> {
                    dispatcher.dispatch(demoPayload)
                }
            }

            it("should throw on self-circular dependencies") {
                var tokenA : UUID? = null
                val demoPayload = object : Payload {}

                tokenA = dispatcher.register {
                    dispatcher.waitFor(tokenA!!)
                    callbackA(demoPayload)
                }

                shouldThrow<IllegalStateException> {
                    dispatcher.dispatch(demoPayload)
                }
                verify(exactly = 0) { callbackA.invoke(any()) }
            }

            it("should throw on multi-circular dependencies") {
                val demoPayload = object : Payload {}
                var tokenB : UUID? = null
                var tokenA : UUID? = null

                tokenA = dispatcher.register {
                    dispatcher.waitFor(tokenB!!)
                    callbackA(demoPayload)
                }

                tokenB = dispatcher.register{
                    dispatcher.waitFor(tokenA!!)
                    callbackB(demoPayload)
                }

                shouldThrow<IllegalStateException> {
                    dispatcher.dispatch(demoPayload)
                }
                verify(exactly = 0) { callbackA.invoke(any()) }
                verify(exactly = 0) { callbackB.invoke(any()) }
            }

            it("should remain in a consistent state after a failed dispatch") {
                class ShouldThrow(val shouldThrow: Boolean): Payload

                dispatcher.register(callbackA)
                dispatcher.register { payload ->
                    if (payload is ShouldThrow && payload.shouldThrow) {
                        throw IllegalStateException()
                    }
                    callbackB(payload)
                }

                shouldThrow<IllegalStateException> {
                    dispatcher.dispatch(ShouldThrow(true))
                }

                // Cannot make assumptions about a failed dispatch.

                dispatcher.dispatch(ShouldThrow(false))

                verify(exactly = 2) { callbackA.invoke(any()) }
                verify(exactly = 1) { callbackB.invoke(any()) }
            }

            it("should properly unregister callbacks") {
                dispatcher.register(callbackA)

                val tokenB = dispatcher.register(callbackB)
                val demoPayload = object : Payload {}
                val demoPayload2 = object : Payload {}

                dispatcher.dispatch(demoPayload)

                verify(exactly = 1) { callbackA.invoke(demoPayload) }
                verify(exactly = 1) { callbackB.invoke(demoPayload) }

                dispatcher.unregister(tokenB)

                dispatcher.dispatch(demoPayload)

                verify(exactly = 2) { callbackA.invoke(any()) }
                verify(exactly = 1) { callbackB.invoke(demoPayload) }
            }
        }
    }
}