package com.lcf.goetia.choreographerhook.reflect

import android.os.Build
import java.lang.reflect.Field
import java.lang.reflect.Method


object ReflectUtils {
    private const val TAG = "Matrix.ReflectUtils"

    @Throws(Exception::class)
    operator fun <T> get(clazz: Class<*>?, fieldName: String?): T? {
        return ReflectFiled<T>(clazz, fieldName).get()
    }

    @Throws(Exception::class)
    operator fun <T> get(clazz: Class<*>?, fieldName: String?, instance: Any?): T? {
        return ReflectFiled<T>(clazz, fieldName)[instance]
    }

    @Throws(Exception::class)
    operator fun set(clazz: Class<*>?, fieldName: String?, `object`: Any?): Boolean {
        return ReflectFiled<Any?>(clazz, fieldName).set(`object`)
    }

    @Throws(Exception::class)
    operator fun set(clazz: Class<*>?, fieldName: String?, instance: Any?, value: Any?): Boolean {
        return ReflectFiled<Any?>(clazz, fieldName).set(instance, value)
    }

    @Throws(Exception::class)
    operator fun <T> invoke(clazz: Class<*>?, methodName: String?, instance: Any?, vararg args: Any?): T? {
        return ReflectMethod(clazz, methodName).invoke<T>(instance, *args)
    }

    fun <T> reflectObject(instance: Any?, name: String?, defaultValue: T, isHard: Boolean): T {
        if (null == instance) return defaultValue
        if (isHard) {
            try {
                val getDeclaredFieldMethod = Class::class.java.getDeclaredMethod("getDeclaredField", String::class.java)
                val field = getDeclaredFieldMethod.invoke(instance.javaClass, name) as Field
                field.isAccessible = true
                return field[instance] as T
            } catch (e: Exception) {
            }
        } else {
            try {
                val field = instance.javaClass.getDeclaredField(name)
                field.isAccessible = true
                return field[instance] as T
            } catch (e: Exception) {
            }
        }
        return defaultValue
    }

    fun <T> reflectObject(instance: Any?, name: String?, defaultValue: T): T {
        return reflectObject(instance, name, defaultValue, true)
    }

    fun reflectMethod(instance: Any, isHard: Boolean, name: String?, vararg argTypes: Class<*>?): Method? {
        if (isHard) {
            try {
                val getDeclaredMethod = Class::class.java.getDeclaredMethod("getDeclaredMethod", String::class.java, arrayOf<Class<*>>()::class.java)
                val method = getDeclaredMethod.invoke(instance.javaClass, name, argTypes) as Method
                method.isAccessible = true
                return method
            } catch (e: Exception) {
            }
        } else {
            try {
                val method = instance.javaClass.getDeclaredMethod(name, *argTypes)
                method.isAccessible = true
                return method
            } catch (e: Exception) {
            }
        }
        return null
    }

    fun reflectMethod(instance: Any, name: String?, vararg argTypes: Class<*>?): Method? {
        val isHard = Build.VERSION.SDK_INT <= 29
        return reflectMethod(instance, isHard, name, *argTypes)
    }
}