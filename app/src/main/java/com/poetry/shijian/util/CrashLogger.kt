package com.poetry.shijian.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 全局未捕获异常记录器。
 * 在 Application.onCreate() 最早处注册，崩溃后将堆栈写入本地文件。
 * 下次启动时自动读取旧日志并输出到 Logcat，方便排查闪退。
 */
object CrashLogger {

    private const val TAG = "Shijian_Crash"
    private const val FILE_NAME = "crash_log.txt"

    fun install(context: Context) {
        val logFile = File(context.filesDir, FILE_NAME)

        // 启动时检查上次是否有崩溃日志
        if (logFile.exists()) {
            try {
                val lastLog = logFile.readText()
                Log.e(TAG, "═══════════════════════════════════════")
                Log.e(TAG, "⚠️ 上次启动发生崩溃，日志如下：")
                Log.e(TAG, lastLog)
                Log.e(TAG, "═══════════════════════════════════════")
                logFile.delete()
            } catch (_: Exception) {}
        }

        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // 保存崩溃日志到文件
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                pw.println("===== 诗笺崩溃日志 =====")
                pw.println("时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(Date())}")
                pw.println("设备: ${android.os.Build.MODEL} (API ${android.os.Build.VERSION.SDK_INT})")
                pw.println()
                throwable.printStackTrace(pw)
                pw.println()
                pw.println("===== 日志结束 =====")
                pw.close()

                logFile.writeText(sw.toString())
                Log.e(TAG, "崩溃日志已保存到: ${logFile.absolutePath}")
            } catch (e: Exception) {
                Log.e(TAG, "保存崩溃日志失败", e)
            }

            // 交给系统默认处理器
            originalHandler?.uncaughtException(thread, throwable)
                ?: kotlin.run {
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(1)
                }
        }

        Log.i(TAG, "崩溃日志记录器已安装")
    }
}
