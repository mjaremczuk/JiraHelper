import api.CredentialsApi
import api.CredentialsService
import api.DatabaseApi
import api.DatabaseService
import api.JiraApi
import api.JiraProjectApi
import api.JiraProjectService
import api.JiraService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module
import presentation.CreateVersionViewModel
import presentation.HomeViewModel
import presentation.SettingsViewModel

expect fun koinModules(): List<Module>

val commonModules = module(true) {
    single<DateProvider> { CurrentDateProvider() }
    single { httpClient() }
    single<JiraApi> { JiraService(get(), get(), get(), get(), Dispatchers.IO) }
    single<JiraProjectApi> { JiraProjectService() }
    single<DatabaseApi> { DatabaseService() }
    single<CredentialsApi> { CredentialsService(get()) }
    factory {
        HomeViewModel(
            jiraApi = get(),
        )
    }
    factory {
        CreateVersionViewModel(
            jiraApi = get(),
            jiraProjectApi = get()
        )
    }
    factory {
        SettingsViewModel(
            credentialsApi = get()
        )
    }
}