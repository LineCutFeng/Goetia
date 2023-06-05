package com.lcf.goetia.choreographerhook.frame.choreographerhook

import android.os.Looper
import android.util.Printer

class LooperMessageTraker(private val looper: Looper) {

    companion object {
        private val looperMessageTrakerLocal = ThreadLocal<LooperMessageTraker>()

        fun getInstance(looper: Looper): LooperMessageTraker {
            var looperMessageTraker = looperMessageTrakerLocal.get()
            if (looperMessageTraker == null) {
                looperMessageTraker = LooperMessageTraker(looper)
                looperMessageTrakerLocal.set(looperMessageTraker)
            }
            return looperMessageTraker
        }
    }

    init {
        looper.setMessageLogging(MessagePinter())
    }

    private val listenerLocal = ThreadLocal<ArrayList<LooperListener>>()

    fun registerListener(listener: LooperListener) {
        var listeners = listenerLocal.get()
        if (listeners == null) {
            listeners = ArrayList()
            listenerLocal.set(listeners)
        }
        listeners.add(listener)
    }

    inner class MessagePinter : Printer {
        override fun println(x: String) {
            val isBegin = x[0] == '>'
            dispatch(isBegin, x)
        }
    }

    private fun dispatch(isBegin: Boolean, originString: String) {
        listenerLocal.get()?.forEach {
            if (isBegin) {
                it.dispatchStart(originString)
            } else {
                it.dispatchEnd(originString)
            }
        }
    }

    interface LooperListener {
        fun dispatchStart(originString: String)
        fun dispatchEnd(originString: String)
    }
}