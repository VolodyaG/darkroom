package ui.histograms

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.converter.NumberStringConverter
import org.controlsfx.control.RangeSlider
import tornadofx.*
import ui.*

val textInputWidth = 50.0

class HistogramPanelView : View() {
    override val root = vbox {
        squeezebox {
            fillHeight = false

            foldwithtoggle("RGB Adjustment", toggleProperty = HistogramEqualizationProperties.applyColorsAdjustment) {
                (graphic as Pane).prefWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH

                vbox {
                    addClass(Styles.boxWithSpacing)

                    imageview(HistogramChartsForFilm.colorHistogramView)
                    colorchannelslider(HistogramEqualizationProperties.redChannelAdjustment, "Cyan", "Red")
                    colorchannelslider(HistogramEqualizationProperties.greenChannelAdjustment, "Magenta", "Green")
                    colorchannelslider(HistogramEqualizationProperties.blueChannelAdjustment, "Yellow", "Blue")

                    HistogramChartsForFilm.colorHistogramView.addListener { _, old, new ->
                        if (old == null && new != null) {
                            isExpanded = true
                        }
                        if (old != null && new == null) {
                            isExpanded = false
                        }
                    }
                }
            }
            foldwithtoggle(
                "Levels Adjustment", toggleProperty = HistogramEqualizationProperties.applyLevelsAdjustment
            ) {
                (graphic as Pane).prefWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH

                gridpane {
                    vgap = 5.0
                    hgap = 5.0

                    row {
                        imageview(HistogramChartsForFilm.greyHistogramView) {
                            fitWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH + 10
                            isPreserveRatio = true

                            gridpaneConstraints {
                                columnSpan = 4
                            }

                            GridPane.setHalignment(this, HPos.CENTER)
                        }
                    }
                    row {
                        rangeslider(0.0, 255.0) {
                            minorTickCount = 0
                            majorTickUnit = 1.0
                            blockIncrement = 1.0
                            isSnapToTicks = true

                            hgrow = Priority.ALWAYS

                            lowValueProperty().bindBidirectional(HistogramEqualizationProperties.lowLumLevel)
                            highValueProperty().bindBidirectional(HistogramEqualizationProperties.highLumLevel)

                            lowValueChangingProperty().addListener { _, old, new ->
                                if (new && old != new && HistogramEqualizationProperties.highLightMaskEnabled()) {
                                    HistogramEqualizationProperties.enableHighlightsMask.set(false)
                                    HistogramEqualizationProperties.enableShadowsMask.set(true)
                                }
                            }
                            highValueChangingProperty().addListener { _, old, new ->
                                if (new && old != new && HistogramEqualizationProperties.shadowsMaskEnabled()) {
                                    HistogramEqualizationProperties.enableShadowsMask.set(false)
                                    HistogramEqualizationProperties.enableHighlightsMask.set(true)
                                }
                            }

                            HistogramEqualizationProperties.lowLumLevel.onChange { setTrackColor() }
                            HistogramEqualizationProperties.highLumLevel.onChange { setTrackColor() }

                            gridpaneConstraints {
                                columnSpan = 4
                            }
                        }
                    }
                    row {
                        textfield {
                            addClass(Styles.sliderTextField)

                            bind(HistogramEqualizationProperties.lowLumLevel, false, NumberStringConverter())

                            GridPane.setMargin(this, Insets(0.0, 0.0, 0.0, 4.0))
                        }
                        togglebutton("S") {
                            graphic = FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE)

                            selectedProperty().bindBidirectional(HistogramEqualizationProperties.enableShadowsMask)
                            action {
                                HistogramEqualizationProperties.enableHighlightsMask.set(false)
                            }
                        }
                        togglebutton("H") {
                            graphic = FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE)

                            selectedProperty().bindBidirectional(HistogramEqualizationProperties.enableHighlightsMask)
                            action {
                                HistogramEqualizationProperties.enableShadowsMask.set(false)
                            }

                            gridpaneColumnConstraints {
                                hgrow = Priority.ALWAYS
                                halignment = HPos.RIGHT
                            }
                        }
                        textfield {
                            addClass(Styles.sliderTextField)

                            bind(HistogramEqualizationProperties.highLumLevel, false, NumberStringConverter())

                            GridPane.setMargin(this, Insets(0.0, 4.0, 0.0, 0.0))
                        }
                    }
                }

                HistogramChartsForFilm.greyHistogramView.addListener { _, old, new ->
                    if (old == null && new != null) {
                        isExpanded = true
                    }
                    if (old != null && new == null) {
                        isExpanded = false
                    }
                }
            }
        }
    }
}

private fun RangeSlider.setTrackColor() {
    val low = HistogramEqualizationProperties.lowLumLevel.value / 255 * 100
    val high = HistogramEqualizationProperties.highLumLevel.value / 255 * 100

    val track = lookup(".track")
    val style = StringBuilder("-fx-background-color: linear-gradient(to right, ")

    style.append("black ").append("0%, ")
    style.append("black ").append(low).append("%, ")
    style.append("white ").append(high).append("%, ")
    style.append("white ").append("100%);")

    track.styleProperty().set(style.toString())
}
