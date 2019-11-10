package ui

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import tornadofx.*
import tornadofx.SqueezeBoxStyles.Companion.squeezeBox

class Styles : Stylesheet() {
    private val middleGray = Color.rgb(71, 71, 71)
    private val darkGray = Color.rgb(51, 51, 51)

    private val themeBlue = Color.rgb(3, 158, 211)
    private val mainBackground = Color.rgb(77, 77, 77)

    companion object {
        val lightGray = Color.rgb(200, 200, 200)

        val mainContainer by cssclass()
        val boxWithSpacing by cssclass()
        val centeredAlignment by cssclass()
        val settingNameLabel by cssclass()
        val resizableRectangle by cssclass()
        val magnifierView by cssclass()
        val rotateSlider by cssclass()
        val infoIcon by cssclass()
        val rangeSlider by cssclass("range-slider")
        val luminosityText by cssclass()
        val lowThumb by cssclass("low-thumb")
        val highThumb by cssclass("high-thumb")
        val rangeBar by cssclass("range-bar")
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
                title {
                    backgroundColor += middleGray
                    fontWeight = FontWeight.BOLD

                    arrowButton {
                        arrow {
                            fill = lightGray
                        }
                    }
                    label {
                        textFill = lightGray
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
            backgroundColor += mainBackground
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
        val thumb = mixin {
            shape = "M 0 2 L 0 5 L 5 5 L 5 2 L 2.5 0 Z"

            and(pressed) {
                backgroundInsets = multi(box(0.px), box(2.px))
            }
        }

        rangeSlider {
            padding = box(0.px, 9.px, 0.px, 6.px)

            track {
                backgroundInsets = multi(box(0.px), box(0.px), box(0.px))
            }
        }
        lowThumb {
            +thumb
            backgroundColor = multi(mainBackground, Color.BLACK)
            backgroundInsets = multi(box(0.px), box(1.5.px))
            backgroundRadius += box(0.px)
        }
        highThumb {
            +thumb
            backgroundColor = multi(lightGray, Color.WHITE)
            backgroundInsets = multi(box(0.px), box(1.5.px))
            backgroundRadius += box(0.px)
        }
        rangeBar {
            backgroundColor += LinearGradient(
                0.0, 0.0, 1.0, 0.0, true,
                CycleMethod.NO_CYCLE,
                Stop(0.0, Color.BLACK), Stop(1.0, Color.WHITE)
            )
        }

        luminosityText {
            maxWidth = 3.4.em
            alignment = Pos.CENTER
        }
    }
}