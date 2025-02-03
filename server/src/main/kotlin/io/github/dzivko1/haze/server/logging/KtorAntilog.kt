package io.github.dzivko1.haze.server.logging

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import io.ktor.util.logging.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern

// Implementation based on Napier DebugAntilog but with KtorSimpleLogger as target logger

class KtorAntilog(
  private val defaultTag: String = "app"
) : Antilog() {

  companion object {
    private const val CALL_STACK_INDEX = 8
  }

  private val logger = KtorSimpleLogger(KtorAntilog::class.simpleName!!)

  private val anonymousClass = Pattern.compile("(\\$\\d+)+$")

  override fun performLog(
    priority: LogLevel,
    tag: String?,
    throwable: Throwable?,
    message: String?,
  ) {
    val debugTag = tag ?: performTag()

    val fullMessage = if (message != null) {
      if (throwable != null) {
        "$message\n${throwable.stackTraceString}"
      } else {
        message
      }
    } else throwable?.stackTraceString ?: return

    val log = buildLog(debugTag, fullMessage)

    when (priority) {
      LogLevel.VERBOSE -> logger.trace(log)
      LogLevel.DEBUG -> logger.debug(log)
      LogLevel.INFO -> logger.info(log)
      LogLevel.WARNING -> logger.warn(log)
      LogLevel.ERROR -> logger.error(log)
      LogLevel.ASSERT -> logger.error(log)
    }
  }

  private fun buildLog(tag: String?, message: String?): String {
    return "${tag ?: performTag()} - $message"
  }

  private fun performTag(): String {
    val thread = Thread.currentThread().stackTrace

    return if (thread.size >= CALL_STACK_INDEX) {
      thread[CALL_STACK_INDEX].run {
        "${createStackElementTag(className)}\$$methodName"
      }
    } else {
      defaultTag
    }
  }

  private fun createStackElementTag(className: String): String {
    var tag = className
    val m = anonymousClass.matcher(tag)
    if (m.find()) {
      tag = m.replaceAll("")
    }
    return tag.substring(tag.lastIndexOf('.') + 1)
  }

  private val Throwable.stackTraceString
    get(): String {
      // DO NOT replace this with Log.getStackTraceString() - it hides UnknownHostException, which is
      // not what we want.
      val sw = StringWriter(256)
      val pw = PrintWriter(sw, false)
      printStackTrace(pw)
      pw.flush()
      return sw.toString()
    }
}