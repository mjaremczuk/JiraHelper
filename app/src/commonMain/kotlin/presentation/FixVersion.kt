package presentation

data class FixVersion(
    val name: String,
    val id: String,
    val archived: Boolean,
    val released: Boolean,
    val url: String,
    val projectId: Int,
)
