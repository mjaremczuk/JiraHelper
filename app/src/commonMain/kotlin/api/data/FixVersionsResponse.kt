package api.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FixVersionsResponse(
    @SerialName("nextPage") val nextPageRequestUrl: String?,
    @SerialName("isLast") val isLast: Boolean,
    @SerialName("values") val fixVersionList: List<FixVersionPojo>,
)
