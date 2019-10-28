package ui

import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import tornadofx.Stylesheet
import tornadofx.cssclass

class Styles : Stylesheet() {
    init {
        propertyLabel {
            fontStyle = FontPosture.ITALIC
            textFill = Color.DARKGRAY
        }
    }

    companion object {
        val propertyLabel by cssclass()
    }
}