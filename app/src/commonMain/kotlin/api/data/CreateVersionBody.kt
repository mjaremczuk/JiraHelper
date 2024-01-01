package api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateVersionBody(
    @SerialName("name") val name: String,
    @SerialName("projectId") val projectId: Int,
)