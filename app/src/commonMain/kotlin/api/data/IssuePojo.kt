package api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssuePojo(
    @SerialName("key") val key: String,
)