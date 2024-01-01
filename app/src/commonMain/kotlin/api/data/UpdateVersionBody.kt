package api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateVersionBody(
    @SerialName("name") val name: String
)