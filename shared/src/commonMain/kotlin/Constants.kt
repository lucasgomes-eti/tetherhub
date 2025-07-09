import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.alternativeParsing
import kotlinx.datetime.format.char

const val SERVER_PORT = 8080
const val PUBLICATION_WORD_LIMIT = 360
val DATE_TIME_PRESENTATION_FORMAT = LocalDateTime.Format {
    alternativeParsing({
        // the day of week may be missing
    }) {
        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(", ")
    }
    dayOfMonth(Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
    char(' ')
    hour()
    char(':')
    minute()
}
const val TERMS_AND_PRIVACY_PATH = "/terms-and-privacy"
const val THIRD_PARTY_SOFTWARE_PATH = "/third-party-software"