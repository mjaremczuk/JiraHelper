sealed class Route(val path: String) {

    data object Home : Route("/home")
    data object EditFixVersion : Route("/version-edit/{index}") {
        const val NAME_PARAMETER = "name"
        fun withParam(index: Int, name: String): String =
            "${EditFixVersion.path}$index?${NAME_PARAMETER}=${name}"
    }

    data object FixVersionTicket : Route("/fix-version/tickets") {
        const val VERSION_NAME_PARAMETER = "versionName"
        fun withParam(name: String): String =
            "${FixVersionTicket.path}?${VERSION_NAME_PARAMETER}=${name}"
    }

    data object CreateFixVersion : Route("/create-fix-version")
    data object Settings : Route("/settings")

}