package io.github.dzivko1.haze.server.util

import java.security.MessageDigest

fun sha256(bytes: ByteArray): ByteArray =
  MessageDigest.getInstance("SHA256").digest(bytes)