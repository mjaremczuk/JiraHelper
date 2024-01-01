import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CurrentDateProvider : DateProvider {

    override fun getDate(): String {
        fun LocalDateTime.format() = toString().substringBefore('T')

        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        return now.toLocalDateTime(zone).format()
    }
}