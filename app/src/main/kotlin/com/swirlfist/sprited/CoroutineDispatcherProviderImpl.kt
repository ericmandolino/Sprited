package com.swirlfist.sprited

import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

class CoroutineDispatcherProviderImpl @Inject constructor() : CoroutineDispatcherProvider {

    override fun getMain() = Dispatchers.Main

    override fun getIO() = Dispatchers.IO

    override fun getDefault() = Dispatchers.Default
}
