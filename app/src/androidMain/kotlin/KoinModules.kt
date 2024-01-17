import api.StorageApi
import com.mjaremczuk.cmm.sample.AndroidStorageProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun koinModules(): List<Module> = listOf(
    androidModule
)

val androidModule = module(true) {
    single<StorageApi> { AndroidStorageProvider(androidApplication()) }
}