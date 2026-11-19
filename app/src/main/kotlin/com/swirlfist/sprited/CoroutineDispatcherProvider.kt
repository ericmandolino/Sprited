package com.swirlfist.sprited

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatcherProvider {
    fun getMain(): CoroutineDispatcher

    fun getIO(): CoroutineDispatcher

    fun getDefault(): CoroutineDispatcher
}
