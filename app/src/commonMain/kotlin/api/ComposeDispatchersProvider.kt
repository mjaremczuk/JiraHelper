package api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class ComposeDispatchersProvider : DispatchersProvider {
    override fun getMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    override fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}