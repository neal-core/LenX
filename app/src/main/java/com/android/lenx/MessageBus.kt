package com.android.lenx

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object MessageBus {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 10);
    val messages = _messages.asSharedFlow();
    fun send(message: String) {
        _messages.tryEmit(message);
    }
}