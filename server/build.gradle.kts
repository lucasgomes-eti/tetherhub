plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
    alias(libs.plugins.serialization)
}

group = "eti.lucasgomes.tetherhub"
version = "1.0.0"
application {
    mainClass.set("eti.lucasgomes.tetherhub.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.koin)
    implementation(libs.ktor.server.koin.logger)
    implementation(libs.ktor.server.bcrypt)
    testImplementation(libs.kotlin.test.junit)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.mongodb)
    implementation(libs.ktor.server.mongodb.bson)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.server.websockets)
}