import api.data.Credentials
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import presentation.FixVersion
import presentation.JiraProject

actual fun saveFixVersions(fixVersions: List<FixVersion>): Boolean {
    return transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(FixVersionDao)

        FixVersionDao.batchUpsert(fixVersions) { fixVersion ->
            this[FixVersionDao.name] = fixVersion.name
            this[FixVersionDao.versionId] = fixVersion.id
            this[FixVersionDao.archived] = fixVersion.archived
            this[FixVersionDao.released] = fixVersion.released
            this[FixVersionDao.url] = fixVersion.url
            this[FixVersionDao.projectId] = fixVersion.projectId
        }
        true
    }
}

actual fun getFixVersionList(): List<FixVersion> {
    return transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(FixVersionDao)

        FixVersionDao.selectAll()
            .map {
                FixVersion(
                    it[FixVersionDao.name].orEmpty(),
                    it[FixVersionDao.versionId],
                    it[FixVersionDao.archived],
                    it[FixVersionDao.released],
                    it[FixVersionDao.url].orEmpty(),
                    it[FixVersionDao.projectId]
                )
            }
    }
}

actual fun updateFixVersionName(
    versionId: List<String>,
    newName: String
) {
    transaction {
        addLogger(StdOutSqlLogger)

        versionId.forEach {
            FixVersionDao.update({ FixVersionDao.versionId eq it }) {
                it[name] = newName
            }
        }
    }
}

actual fun removeFixVersions(versionId: List<String>) {
    transaction {
        addLogger(StdOutSqlLogger)

        FixVersionDao.deleteWhere { FixVersionDao.versionId inList versionId }
    }
}

fun getDesktopApiCredentials(): Credentials? {
    return transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(CredentialsDao)
        val allResults = CredentialsDao.selectAll()
        println("results: ${allResults.fetchSize}")
        allResults.firstOrNull()?.let {
            println("results: $it")
            val valid =
                it[CredentialsDao.name].orEmpty().isNotBlank() && it[CredentialsDao.token].orEmpty()
                    .isNotBlank()
            if (valid) {
                Credentials(
                    it[CredentialsDao.id].value,
                    it[CredentialsDao.name].orEmpty(),
                    it[CredentialsDao.token].orEmpty(),
                    it[CredentialsDao.baseUrl].orEmpty(),
                )
            } else {
                null
            }
        }
    }
}

fun updateDesktopApiCredentials(credentials: Credentials) {
    return transaction {
        addLogger(StdOutSqlLogger)
        if (credentials.id != null) {
            CredentialsDao.upsert {
                it[id] = credentials.id
                it[name] = credentials.username
                it[token] = credentials.token
                it[baseUrl] = credentials.baseUrl
            }
        } else {
            CredentialsDao.insertAndGetId {
                it[name] = credentials.username
                it[token] = credentials.token
                it[baseUrl] = credentials.baseUrl
            }
        }
    }
}

fun addDesktopProject(project: JiraProject) {
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ProjectDao)
        val number = ProjectDao.insert {
            it[id] = project.id
            it[key] = project.key
            it[name] = project.name
        }
        println("insterted records: $number")
    }
}

fun getDesktopProjects(): List<JiraProject> {
    return transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ProjectDao)
        ProjectDao.selectAll()
            .map {
                JiraProject(
                    it[ProjectDao.name].orEmpty(),
                    it[ProjectDao.key],
                    it[ProjectDao.id],
                )
            }
    }
}

object FixVersionDao : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 150).nullable()
    val versionId = varchar("version_id", 50).uniqueIndex()
    val archived = bool("archived")
    val released = bool("released")
    val url = varchar("url", 254).nullable()
    val projectId = integer("project_id")
}

object CredentialsDao : IntIdTable() {
    val name = varchar("username", 150).nullable()
    val token = varchar("token", 500).nullable()
    val baseUrl = varchar("base_url", 150).nullable()
}

object ProjectDao : Table() {
    val id = integer("id").uniqueIndex()
    val key = varchar("key", 10)
    val name = varchar("name", 100).nullable()
}