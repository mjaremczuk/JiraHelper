import api.StorageApi
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun koinModules(): List<Module> = listOf(desktopModules)

val desktopModules = module(true) {
    single<StorageApi> { DesktopStorageProvider() }
}