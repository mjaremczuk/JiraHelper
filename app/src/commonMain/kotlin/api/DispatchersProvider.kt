package api

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {

    fun getMainDispatcher(): CoroutineDispatcher
    fun getIoDispatcher(): CoroutineDispatcher
}