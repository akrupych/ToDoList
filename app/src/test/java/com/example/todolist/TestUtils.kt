package com.example.todolist

import com.example.todolist.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest

@ExperimentalCoroutinesApi
object TestDispatcherProvider : DispatcherProvider {

    val testDispatcher = TestCoroutineDispatcher()

    override val Main: CoroutineDispatcher
        get() = testDispatcher
    override val IO: CoroutineDispatcher
        get() = testDispatcher
    override val Default: CoroutineDispatcher
        get() = testDispatcher
    override val Unconfined: CoroutineDispatcher
        get() = testDispatcher
}

@ExperimentalCoroutinesApi
fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
    TestDispatcherProvider.testDispatcher.runBlockingTest(block)