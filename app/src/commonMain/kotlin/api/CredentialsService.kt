package api

import api.data.Credentials

class CredentialsService(private val databaseApi: DatabaseApi) : CredentialsApi {

    private var saved: Credentials? = null

    override suspend fun getCredentials(): Credentials {
        saved = databaseApi.getCredentials()
        return saved ?: throw MissingCredentialsException()
    }

    override suspend fun updateCredentials(credentials: Credentials) {
        databaseApi.updateCredentials(credentials)
        saved = credentials
    }
}