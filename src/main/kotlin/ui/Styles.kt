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
    private val themeBlue = Color.rgb(45, 168, 209)
    private val mainBackground = Color.rgb(77, 77, 77)

    companion object {
        val lightGray = Color.rgb(200, 200, 200)

        val mainContainer by cssclass()
        val boxWithSpacing by cssclass()
        val centeredAlignment by cssclass()
        val sliderTextField by cssclass()
        val settingNameLabel by cssclass()
        val resizableRectangle by cssclass()
        val magnifierView by cssclass()
        val dimensionsLabel by cssclass()
        val filmTypeContainer by cssclass()
        val rotateSlider by cssclass()
        val infoIcon by cssclass()
        val galleryLoadProgress by cssclass()
        val galleryScrollContainer by cssclass()
        val gallery by cssclass()
        val scanButton by cssclass()
        val rangeSlider by cssclass("range-slider")
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

            backgroundColor += lightGray.derive(0.5)
            textFill = middleGray

            and(hover) {
                backgroundColor += lightGray.derive(0.1)
            }

            and(pressed) {
                backgroundColor += lightGray.derive(-0.1)
            }
        }
        button {
            +buttonBase
        }
        toggleButton {
            +buttonBase

            and(selected) {
                backgroundColor += lightGray.derive(-0.1)
            }
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
                            backgroundColor += lightGray
                            backgroundInsets += box(0.px)
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
        sliderTextField {
            maxWidth = 3.4.em
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
        dimensionsLabel {
            textFill = lightGray
        }
    }

    private fun initSettingsPanelStyles() {
        filmTypeContainer {
            radioButton {
                textFill = lightGray
            }
        }
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
        galleryLoadProgress {
            progressColor = Color.WHITE
        }
        galleryScrollContainer {
            borderWidth = multi(box(0.px, 1.px, 1.px, 1.px))
            borderColor += box(lightGray)
            backgroundInsets = multi(box(0.px))
        }
        gallery {
            vgap = 4.px
            hgap = 4.px
            padding = box(5.px, 0.px, 5.px, 5.px)
        }
        scanButton {
            backgroundColor += themeBlue
            textFill = Color.WHITE

            and(hover) {
                backgroundColor += themeBlue.derive(-0.2)
            }

            and(pressed) {
                backgroundColor += themeBlue.derive(-0.4)
            }
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
    }
}