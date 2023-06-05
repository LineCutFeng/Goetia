package com.lcf.goetia.choreographerhook.frame.choreographerhook

import android.content.Context
import android.os.Looper
import android.util.Log
import android.util.SparseIntArray
import android.view.Choreographer
import android.view.WindowManager
import com.lcf.goetia.choreographerhook.reflect.ReflectUtils
import java.lang.reflect.Method

class FrameTracker : LooperMessageTraker.LooperListener {

    var frameIntervalNanos = 16666667L
    private val CALLBACK_INPUT = 0

    private val tag = "MatrixFrameTracker"

    private val addCallbackMethodName = "addCallbackLocked"

    private lateinit var callbackQueueLock: Any
    private var callbackQueues: Array<Any>? = null
    private var addInputQueueMethod: Method? = null
    private var vsyncReceiver: Any? = null

    private var isVsyncFrame: Boolean = false

    private var startTime = 0L

    companion object {
        private val frameTracker: FrameTracker = FrameTracker()
        fun getFrameTracker(cxt: Context): FrameTracker {
            frameTracker.init(cxt)
            return frameTracker
        }
    }

    private var isInit = false
    fun init(cxt: Context) {
        if (isInit) {
            return
        }
        isInit = true
        initReflect(cxt)
        addInputCallBack {
            isVsyncFrame = true
        }
        LooperMessageTraker.getInstance(Looper.getMainLooper()).registerListener(this)
    }

    private fun initReflect(cxt: Context) {
        val choreographer = Choreographer.getInstance()

        frameIntervalNanos = (1000000000 / (cxt.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.mode.refreshRate).toLong()
//        frameIntervalNanos = ReflectUtils.reflectObject(choreographer, "mFrameIntervalNanos", 16666667L)
        vsyncReceiver = ReflectUtils.reflectObject<Any?>(choreographer, "mDisplayEventReceiver", null)
        callbackQueueLock = ReflectUtils.reflectObject(choreographer, "mLock", Any())
        callbackQueues = ReflectUtils.reflectObject<Array<Any>?>(choreographer, "mCallbackQueues", null)


        callbackQueues?.getOrNull(CALLBACK_INPUT)?.let {
            addInputQueueMethod = ReflectUtils.reflectMethod(
                it,
                addCallbackMethodName,
                Long::class.javaPrimitiveType,
                Any::class.java,
                Any::class.java
            )
        }
    }

    private fun addInputCallBack(callback: Runnable) {
        try {
            synchronized(callbackQueueLock) {

                callbackQueues?.getOrNull(CALLBACK_INPUT)?.let {
                    addInputQueueMethod?.invoke(it, -1, callback, null)
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    override fun dispatchStart(originString: String) {
        Log.d(tag, originString)
        startTime = System.nanoTime()
    }

    override fun dispatchEnd(originString: String) {
        if (isVsyncFrame) {
            val endNs = System.nanoTime()
            val intendedFrameTimeNs = getIntendedFrameTimeNs(startTime)
            val jit = endNs - intendedFrameTimeNs
            val dropFrame = ((jit) / frameIntervalNanos).toInt()
            notifyListener(intendedFrameTimeNs, endNs, dropFrame)
            addInputCallBack { isVsyncFrame = true }
        }
        isVsyncFrame = false
    }

    private fun getIntendedFrameTimeNs(defaultValue: Long): Long {
        try {
            return ReflectUtils.reflectObject(vsyncReceiver, "mTimestampNanos", defaultValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultValue
    }

    private fun notifyListener(startNs: Long, endNs: Long, dropFrame: Int) {
        mListenersLocal.get()?.forEach {
            it.onFrame(startNs, endNs, dropFrame)
        }
    }

    private val mListenersLocal = ThreadLocal<ArrayList<FrameListener>>()

    fun register(listener: FrameListener) {
        var listeners = mListenersLocal.get()
        if (listeners == null) listeners = ArrayList()
        mListenersLocal.set(listeners)

        listeners.add(listener)
    }

    fun unregister(listener: FrameListener) {
        val listeners: ArrayList<FrameListener>? = mListenersLocal.get() ?: return
        listeners?.remove(listener)
    }

    interface FrameListener {
        fun onFrame(startNs: Long, endNs: Long, dropFrame: Int)
    }

//    class FPSListener : FrameListener {
//        private val ary = SparseIntArray()
//        fun getTotalInfo(): SparseIntArray {
//            return ary;
//        }
//
//        override fun onFrame(startNs: Long, endNs: Long, droppedFrames: Int) {
//            val duration = ((endNs - startNs) / 1000000f).toInt()
//            var count = ary.get(duration)
//            ary.put(duration, ++count)
//        }
//    }
//
//    enum class DropStatus(var index: Int) {
//        DROPPED_FROZEN(4),
//        DROPPED_HIGH(3),
//        DROPPED_MIDDLE(2),
//        DROPPED_NORMAL(1),
//        DROPPED_BEST(0);
//    }

}