package api.data

data class Credentials(
    val id: Int?,
    val username: String,
    val token: String,
    val baseUrl: String,
) {

    companion object {
        fun default() = Credentials(null, "", "", "")
    }
}
