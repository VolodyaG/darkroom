package ui.selection

data class Dimensions(val horizontal: Int, val vertical: Int) {
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is Dimensions) {
            return false
        }

        return horizontal == other.horizontal && vertical == other.vertical
    }
}
