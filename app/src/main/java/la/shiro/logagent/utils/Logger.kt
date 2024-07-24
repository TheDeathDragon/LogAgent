package la.shiro.logagent.utils

import android.util.Log

object Logger {

    private const val TAG = "LogAgent"

    private var isDebug = true

    private fun getCallerInfo(): String {
        val stackTrace = Throwable().stackTrace
        for (element in stackTrace) {
            if (element.className != this::class.java.name && element.className.indexOf("java.lang.Thread") != 0) {
                val className = element.className.substringAfterLast(".").substringBefore("$")
                return "$className --> ${element.methodName}"
            }
        }
        return "Unknown"
    }

    fun d(message: String) {
        if (isDebug) {
            Log.d(TAG, "${getCallerInfo()} --> $message")
        }
    }

    fun d() {
        if (isDebug) {
            Log.d(TAG, getCallerInfo())
        }
    }

    fun v(message: String) {
        if (isDebug) {
            Log.d(TAG, "${getCallerInfo()} --> $message")
        }
    }

    fun v() {
        if (isDebug) {
            Log.d(TAG, getCallerInfo())
        }
    }

    fun i(message: String) {
        if (isDebug) {
            Log.d(TAG, "${getCallerInfo()} --> $message")
        }
    }

    fun i() {
        if (isDebug) {
            Log.d(TAG, getCallerInfo())
        }
    }

    fun w(message: String) {
        if (isDebug) {
            Log.d(TAG, "${getCallerInfo()} --> $message")
        }
    }

    fun w() {
        if (isDebug) {
            Log.d(TAG, getCallerInfo())
        }
    }

    fun e(message: String) {
        if (isDebug) {
            Log.d(TAG, "${getCallerInfo()} --> $message")
        }
    }

    fun e() {
        if (isDebug) {
            Log.d(TAG, getCallerInfo())
        }
    }

    fun setDebugMode(isDebug: Boolean) {
        Logger.isDebug = isDebug
    }
}