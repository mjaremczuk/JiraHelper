package api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FixVersionPojo(
    @SerialName("name") val name: String,
    @SerialName("id") val id: String,
    @SerialName("archived") val archived: Boolean,
    @SerialName("released") val released: Boolean,
    @SerialName("self") val url: String,
    @SerialName("projectId") val projectId: Long,
)
