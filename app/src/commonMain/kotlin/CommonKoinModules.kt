import api.ComposeDispatchersProvider
import api.CredentialsApi
import api.CredentialsService
import api.DatabaseApi
import api.DatabaseService
import api.DispatchersProvider
import api.JiraApi
import api.JiraProjectApi
import api.JiraProjectService
import api.JiraService
import org.koin.core.module.Module
import org.koin.dsl.module
import presentation.AddProjectViewModel
import presentation.CreateVersionViewModel
import presentation.HomeViewModel
import presentation.SettingsViewModel

expect fun koinModules(): List<Module>

val commonModules = module(true) {
    single<DateProvider> { CurrentDateProvider() }
    single { httpClient() }
    single<DispatchersProvider> { ComposeDispatchersProvider() }
    single<JiraApi> { JiraService(get(), get(), get(), get(), get()) }
    single<JiraProjectApi> { JiraProjectService(get()) }
    single<DatabaseApi> { DatabaseService(get()) }
    single<CredentialsApi> { CredentialsService(get()) }
    factory {
        HomeViewModel(
            jiraApi = get(),
        )
    }
    factory {
        CreateVersionViewModel(
            jiraApi = get(),
            jiraProjectApi = get(),
        )
    }
    factory {
        SettingsViewModel(
            credentialsApi = get(),
            dispatchersProvider = get(),
        )
    }
    factory {
        AddProjectViewModel(
            projectApi = get(),
            dispatchersProvider = get(),
        )
    }
}