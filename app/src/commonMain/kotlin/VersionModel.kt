import androidx.compose.runtime.Stable

@Stable
data class VersionModel(
    val name: String,
    val projects: List<VersionProjectModel>,
)

@Stable
data class VersionProjectModel(
    val key: String,
    val projectId: Int,
    val versionId: String,
    val selected: Boolean,
    val released: Boolean,
    val archived: Boolean,
)