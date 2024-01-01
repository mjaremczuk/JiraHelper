package presentation

data class JiraProject(
    val name: String,
    val key: String,
    val id: Long,
    val selected: Boolean = true,
)
