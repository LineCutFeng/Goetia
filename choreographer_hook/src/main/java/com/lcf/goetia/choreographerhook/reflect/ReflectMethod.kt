package com.lcf.goetia.choreographerhook.reflect

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class ReflectMethod(clazz: Class<*>?, methodName: String?, vararg parameterTypes: Class<*>) {
    private val mClazz: Class<*>
    private val mMethodName: String
    private var mInit = false
    private var mMethod: Method? = null
    private val mParameterTypes: Array<out Class<*>>

    init {
        require(clazz == null || methodName == null || methodName.isNotEmpty()) { "Both of invoker and fieldName can not be null or nil." }
        mClazz = clazz!!
        mMethodName = methodName!!
        mParameterTypes = parameterTypes
    }

    @Synchronized
    private fun prepare() {
        if (mInit) {
            return
        }
        var clazz: Class<*>? = mClazz
        while (clazz != null) {
            try {
                val method: Method = clazz.getDeclaredMethod(mMethodName, *mParameterTypes)
                method.setAccessible(true)
                mMethod = method
                break
            } catch (e: Exception) {
            }
            clazz = clazz.superclass
        }
        mInit = true
    }

    @Synchronized
    @Throws(NoSuchFieldException::class, IllegalAccessException::class, IllegalArgumentException::class, InvocationTargetException::class)
    operator fun <T> invoke(instance: Any?, vararg args: Any?): T? {
        return invoke(instance, false, args)
    }

    @Synchronized
    @Throws(NoSuchFieldException::class, IllegalAccessException::class, IllegalArgumentException::class, InvocationTargetException::class)
    operator fun <T> invoke(instance: Any?, ignoreFieldNoExist: Boolean, vararg args: Any?): T? {
        prepare()
        if (mMethod == null) {
            if (!ignoreFieldNoExist) {
                throw NoSuchFieldException("Method $mMethodName is not exists.")
            }
            return null
        }
        return mMethod?.invoke(instance, args) as? T
    }

    @Synchronized
    fun <T> invokeWithoutThrow(instance: Any?, vararg args: Any?): T? {
        try {
            return invoke<T>(instance, true, *args)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: InvocationTargetException) {
        }
        return null
    }

    companion object {
        private const val TAG = "ReflectFiled"
    }
}