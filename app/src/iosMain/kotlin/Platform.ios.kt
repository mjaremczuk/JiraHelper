import api.data.Credentials
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice
import presentation.FixVersion

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override val type: Type
        get() = Type.IOS
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
    config(this)
    engine {
        configureRequest {
            setAllowsCellularAccess(true)
        }
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

actual fun getApiCredentials(): Credentials? {
    return null
}

actual fun updateApiCredentials(credentials: Credentials) {

}