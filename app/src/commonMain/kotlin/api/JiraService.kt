package api

import VersionModel
import VersionProjectModel
import api.data.CreateVersionBody
import api.data.Credentials
import api.data.FixVersionPojo
import api.data.FixVersionsResponse
import api.data.SearchResponse
import api.data.UpdateVersionBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.basicAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import presentation.FixVersion
import presentation.JiraProject

class JiraService(
    private val client: HttpClient,
    private val jiraProjectApi: JiraProjectApi,
    private val databaseApi: DatabaseApi,
    private val credentialsApi: CredentialsApi,
    private val dispatchersProvider: DispatchersProvider,
) : JiraApi {

    override suspend fun removeFixVersion(versionModel: VersionModel) {
        withContext(dispatchersProvider.getIoDispatcher()) {
            val credentials = credentialsApi.getCredentials()
            val result = versionModel.projects.map {
                async {
                    removeVersion(it.versionId, credentials)
                }
            }.awaitAll()

            if (result.all { it.isSuccess() } || result.all { it == NotFound }) {
                databaseApi.removeVersions(versionModel.projects.map { it.versionId })
            }
        }
    }

    override suspend fun getFixVersions(forceRefresh: Boolean): List<VersionModel> {
        return withContext(dispatchersProvider.getIoDispatcher()) {
            val projects = jiraProjectApi.getProjects()
            if (forceRefresh.not()) {
                val savedVersions = databaseApi.getFixVersions()
                if (savedVersions.isNotEmpty()) {
                    return@withContext savedVersions.asSequence()
                        .toVersionModel(projects)
                }
            }
            val credentials = credentialsApi.getCredentials()
            val results = projects.map {
                async { getFixVersionsForProject(it, credentials) }
            }.awaitAll()

            results
                .asSequence()
                .flatten()
                .sortedByDescending { it.id }
                .map { it.toVersion() }
                .also {
                    databaseApi.save(it.toList())
                }
                .toVersionModel(projects)
        }
    }

    override suspend fun updateVersion(value: VersionModel, new: String) {
        withContext(dispatchersProvider.getIoDispatcher()) {
            val credentials = credentialsApi.getCredentials()
            val result = value.projects.map {
                async {
                    updateVersion(it.versionId, new, credentials)
                }
            }.awaitAll()

            if (result.all { it.value == HttpStatusCode.OK.value }) {
                databaseApi.updateVersionName(value.projects.map { it.versionId }, new)
            }
        }
    }

    override suspend fun createFixVersion(fixVersionName: String, selectedProjects: List<Int>) {
        withContext(dispatchersProvider.getIoDispatcher()) {
            val credentials = credentialsApi.getCredentials()
            val result = selectedProjects.map {
                async {
                    createVersion(fixVersionName, it, credentials)
                }
            }.awaitAll()


            result.map { it.toVersion() }.also {
                databaseApi.save(it)
            }
        }
    }

    override suspend fun getTicketsFor(name: String): List<String> {
        val credentials = credentialsApi.getCredentials()
        val result =
            client.get(credentials.buildApiUrl("search")) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                parameter("jql", "fixVersion=\"$name\"")

                authorize(credentials)
            }
        println("Search tickets for version $name  result: ${result.status}")
        if (result.status.isSuccess()) {
            return result.body<SearchResponse>().issues.map {
                credentials.buildIssueUrl(it.key)
            }
        } else {
            throw IllegalStateException(result.body<String>())
        }
    }

    private suspend fun createVersion(
        name: String,
        projectId: Int,
        credentials: Credentials
    ): FixVersionPojo {
        val result = client.post(credentials.buildApiUrl("version")) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            setBody(CreateVersionBody(name, projectId))
            authorize(credentials)
        }
        println("Create version $name in $projectId name result: $result")
        if (result.status.isSuccess()) {
            return result.body<FixVersionPojo>()
        } else {
            throw IllegalStateException(result.body<String>())
        }
    }

    private suspend fun getFixVersionsForProject(
        project: JiraProject,
        credentials: Credentials
    ): List<FixVersionPojo> {
        return fetchFixVersions(
            emptyList(),
            credentials.buildApiUrl("project/${project.key}/version"),
            credentials
        )
    }

    private suspend fun fetchFixVersions(
        startingList: List<FixVersionPojo>,
        url: String,
        credentials: Credentials,
    ): List<FixVersionPojo> {
        val result = client.get(url) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            authorize(credentials)
        }
        if (result.status.isSuccess()) {
            val body = result.body<FixVersionsResponse>()

            val updatedList = startingList.plus(body.fixVersionList)
            return if (body.isLast || body.nextPageRequestUrl == null) {
                updatedList
            } else {
                println("requesting data for: ${body.nextPageRequestUrl}")
                fetchFixVersions(updatedList, body.nextPageRequestUrl, credentials)
            }
        } else {
            throw IllegalStateException(result.bodyAsText())
        }
    }

    private suspend fun updateVersion(
        versionId: String,
        name: String,
        credentials: Credentials
    ): HttpStatusCode {
        val result = client.put(credentials.buildApiUrl("version/$versionId")) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            setBody(UpdateVersionBody(name))
            authorize(credentials)
        }
        println("update version $versionId name result: $result")
        return result.status
    }

    private suspend fun removeVersion(id: String, credentials: Credentials): HttpStatusCode {
        val result = client.delete(credentials.buildApiUrl("version/$id")) {
            authorize(credentials)
        }
        println("update version $id name result: $result")
        return result.status
    }

    private suspend fun Sequence<FixVersion>.toVersionModel(projects: List<JiraProject>): List<VersionModel> {
        return groupBy {
            it.name
        }
            .map {
                VersionModel(
                    it.key,
                    it.value.mapNotNull { fixVersion ->
                        val project = projects
                            .firstOrNull { project -> project.id == fixVersion.projectId }
                            ?: return@mapNotNull null
                        VersionProjectModel(
                            project.key,
                            fixVersion.projectId,
                            fixVersion.id,
                            project.selected,
                            fixVersion.released,
                            fixVersion.archived,
                        )
                    }
                )
            }.toList()
            .sortedBy { it.name }
    }

    private fun FixVersionPojo.toVersion() =
        FixVersion(
            name = name,
            id = id,
            archived = archived,
            released = released,
            url = url,
            projectId = projectId,
        )

    private fun HttpMessageBuilder.authorize(credentials: Credentials) {
        basicAuth(
            credentials.username,
            credentials.token
        )
    }

    private fun Credentials.buildApiUrl(path: String) = "$baseUrl/rest/api/3/$path"
    private fun Credentials.buildIssueUrl(key: String) = "$baseUrl/browse/$key"
}

