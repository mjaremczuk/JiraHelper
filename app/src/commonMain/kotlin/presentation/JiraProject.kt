package presentation

data class JiraProject(
    val name: String,
    val key: String,
    val id: Int,
    val selected: Boolean = true,
)
