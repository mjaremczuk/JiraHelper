package api

import api.data.Credentials

interface CredentialsApi {

    suspend fun getCredentials(): Credentials
    suspend fun updateCredentials(credentials: Credentials)
}