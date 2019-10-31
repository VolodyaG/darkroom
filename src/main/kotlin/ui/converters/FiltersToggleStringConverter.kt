package ui.converters

import javafx.util.StringConverter

class FiltersToggleStringConverter: StringConverter<Boolean>() {
    override fun toString(filtersApplied: Boolean): String {
        return if (filtersApplied) toggledText else untoggledText
    }

    override fun fromString(string: String): Boolean {
        return string.equals(toggledText, ignoreCase = true)
    }

    companion object {
        const val toggledText = "Filters ON"
        const val untoggledText = "Filters OFF"
    }
}