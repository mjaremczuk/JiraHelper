import api.data.Credentials
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import presentation.FixVersion

enum class Type {
    ANDROID_MOBILE,
    IOS,
    DESKTOP,
}

interface Platform {
    val name: String
    val type: Type
}

expect fun getPlatform(): Platform

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

expect fun saveFixVersions(fixVersions: List<FixVersion>): Boolean

expect fun getFixVersionList(): List<FixVersion>

expect fun updateFixVersionName(versionId: List<String>, newName: String)

expect fun removeFixVersions(versionId: List<String>)
