package ui.histograms

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.geometry.HPos
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.converter.NumberStringConverter
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

                vbox {
                    addClass(Styles.boxWithSpacing)

                    imageview(HistogramChartsForFilm.greyHistogramView)
                    hbox {
                        addClass(Styles.centeredAlignment)

                        textfield {
                            maxWidth = textInputWidth
                            bind(HistogramEqualizationProperties.lowLumLevel, false, NumberStringConverter())
                        }
                        rangeslider(0.0, 1.0) {
                            addClass(Styles.rangeSlider)

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
                        }
                        textfield {
                            maxWidth = textInputWidth
                            bind(HistogramEqualizationProperties.highLumLevel, false, NumberStringConverter())
                        }
                    }
                    gridpane {
                        row {
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
}
