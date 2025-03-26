package kg.automoika.di

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AppData {

    private val messageResponseFlow = MutableSharedFlow<String>()
    val sharedFlow = messageResponseFlow.asSharedFlow()


    suspend fun sendMessage(message : String){
        messageResponseFlow.emit(message)
    }
}