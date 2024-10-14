import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.ktor)
  application
}

group = "io.github.dzivko1.haze"
version = libs.versions.appVersionName.get()
application {
  mainClass.set("io.github.dzivko1.haze.server.ApplicationKt")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
  compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
}

dependencies {
  implementation(projects.shared)
  implementation(libs.kotlinx.serialization.json)
  implementation(project.dependencies.platform(libs.koin.bom))
  implementation(libs.koin.core)
  implementation(libs.koin.ktor)
  implementation(libs.logback)
  implementation(libs.ktor.server.core)
  implementation(libs.ktor.server.netty)
  implementation(libs.ktor.server.contentNegotiation)
  implementation(libs.ktor.server.auth)
  implementation(libs.ktor.server.auth.jwt)
  implementation(libs.ktor.server.statusPages)
  implementation(libs.ktor.serialization.kotlinx.json)
  implementation(libs.exposed.core)
  implementation(libs.exposed.jdbc)
  implementation(libs.exposed.dao)
  implementation(libs.postgresql)

  testImplementation(libs.ktor.server.tests)
  testImplementation(libs.kotlin.test.junit)
}