@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"

    override val type: Type
        get() = Type.DESKTOP
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(CIO) {
    config(this)
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = true
            isLenient = true
        })
        addDefaultResponseValidation()
    }
}
