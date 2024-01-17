package com.mjaremczuk.cmm.sample

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import api.StorageApi
import api.data.Credentials
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import presentation.JiraProject

class AndroidStorageProvider(private val context: Context) : StorageApi {

    companion object {
        private val CREDENTIALS_KEY = stringPreferencesKey("credential_key")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
    override suspend fun getApiCredentials(): Credentials? {
        return context.dataStore.data.map {
            Json.decodeFromString<Credentials?>(it[CREDENTIALS_KEY].orEmpty())
        }.firstOrNull()
    }

    override suspend fun updateApiCredentials(credentials: Credentials) {
        context.dataStore.edit {
            it[CREDENTIALS_KEY] = Json.encodeToString<Credentials>(credentials)
        }
    }

    override fun getProjects(): List<JiraProject> {
        return emptyList()
    }

    override fun addProject(project: JiraProject) {
    }
}