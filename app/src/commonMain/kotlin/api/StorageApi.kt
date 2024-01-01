package api

import api.data.Credentials

interface StorageApi {
    suspend fun getApiCredentials(): Credentials?
    suspend fun updateApiCredentials(credentials: Credentials)
}