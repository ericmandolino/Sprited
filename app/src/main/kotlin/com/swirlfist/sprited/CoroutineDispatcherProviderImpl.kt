package com.swirlfist.sprited

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatcherProviderImpl @Inject constructor() : CoroutineDispatcherProvider {

    override fun getMain() = Dispatchers.Main

    override fun getIO() = Dispatchers.IO

    override fun getDefault() = Dispatchers.Default
}