package io.github.dzivko1.haze

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform