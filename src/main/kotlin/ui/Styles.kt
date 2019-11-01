package ui

import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class Styles : Stylesheet() {
    init {
        mainContainer {
            backgroundColor += Color.rgb(47, 52, 57)
            hgap = 10.px
            vgap = 10.px
            padding = box(10.px)
        }
        propertyLabel {
            fontStyle = FontPosture.ITALIC
            textFill = Color.DARKGRAY
        }
    }

    companion object {
        val mainContainer by cssclass()
        val propertyLabel by cssclass()
    }
}