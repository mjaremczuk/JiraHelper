import android.os.Build
import api.data.Credentials
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import presentation.FixVersion
import java.util.concurrent.TimeUnit

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override val type: Type
        get() = Type.ANDROID_MOBILE
}

actual fun getPlatform(): Platform = AndroidPlatform()

@OptIn(ExperimentalSerializationApi::class)
actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    config(this)
    engine {
        config {
            retryOnConnectionFailure(true)
            connectTimeout(5, TimeUnit.SECONDS)
        }
    }
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

actual fun saveFixVersions(fixVersions: List<FixVersion>): Boolean {
    return false
}

actual fun getFixVersionList(): List<FixVersion> {
    return emptyList()
}

actual fun updateFixVersionName(
    versionId: List<String>,
    newName: String
) {
}

actual fun removeFixVersions(versionId: List<String>) {

}