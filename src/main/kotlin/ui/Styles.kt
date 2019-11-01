package ui

import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*
import tornadofx.SqueezeBoxStyles.Companion.squeezeBox

class Styles : Stylesheet() {
    private val lightGray = Color.rgb(200, 200, 200)
    private val middleGray = Color.rgb(71, 71, 71)
    private val darkGray = Color.rgb(51, 51, 51)

    private val themeBlue = Color.rgb(3, 158, 211)

    init {
        // built in styles
        val buttonBase = mixin {
            backgroundRadius += box(0.px)
            backgroundInsets += box(0.px)
            borderColor += box(Color.TRANSPARENT)
            borderWidth += box(1.px)
        }
        button {
            +buttonBase
        }
        toggleButton {
            +buttonBase
        }
        textInput {
            backgroundRadius += box(0.px)
        }
        squeezeBox {
            titledPane {
                textFill = lightGray

                title {
                    backgroundColor += middleGray
                    fontWeight = FontWeight.BOLD

                    arrowButton {
                        arrow {
                            fill = lightGray
                        }
                    }
                }
            }
        }

        // custom styles
        mainContainer {
            backgroundColor += Color.rgb(77, 77, 77)
            hgap = 10.px
            vgap = 10.px
            padding = box(10.px)
        }
        boxWithSpacing {
            spacing = 5.px
        }
        propertyLabel {
            fontStyle = FontPosture.ITALIC
            textFill = Color.DARKGRAY
        }
    }

    companion object {
        val mainContainer by cssclass()
        val boxWithSpacing by cssclass()
        val propertyLabel by cssclass()
    }
}