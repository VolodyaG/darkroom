package ui

import javafx.geometry.Pos
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

    companion object {

        val mainContainer by cssclass()
        val boxWithSpacing by cssclass()
        val centeredAlignment by cssclass()
        val settingNameLabel by cssclass()
        val resizableRectangle by cssclass()
        val magnifierView by cssclass()
        val rotateSlider by cssclass()
        val infoIcon by cssclass()
        val rangeSlider by cssclass()
    }

    init {
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

        boxWithSpacing {
            spacing = 5.px
        }
        centeredAlignment {
            alignment = Pos.CENTER
        }

        initMainViewStyles()
        initSettingsPanelStyles()
        initHistogramPanelStyles()
    }

    private fun initMainViewStyles() {
        mainContainer {
            backgroundColor += Color.rgb(77, 77, 77)
            hgap = 10.px
            vgap = 10.px
            padding = box(10.px)
        }
        resizableRectangle {
            stroke = themeBlue
            strokeWidth = 1.px
            fill = Color(1.0, 1.0, 1.0, 0.0)
        }
        magnifierView {
            backgroundColor += Color.WHITE
            borderWidth += box(3.px)
            borderColor += box(themeBlue)
        }
    }

    private fun initSettingsPanelStyles() {
        settingNameLabel {
            fontStyle = FontPosture.ITALIC
            textFill = Color.DARKGRAY
        }
        rotateSlider {
            padding = box(0.px, 5.px, 0.px, 5.px)
        }
        infoIcon {
            fontSize = 16.px
        }
    }

    private fun initHistogramPanelStyles() {
        rangeSlider {
            padding = box(0.px, 5.px, 0.px, 2.px)
        }
    }
}