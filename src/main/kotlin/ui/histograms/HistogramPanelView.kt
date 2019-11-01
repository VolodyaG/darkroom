package ui.histograms

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import org.controlsfx.control.RangeSlider
import tornadofx.*
import ui.LEFT_AND_RIGHT_WINDOWS_WIDTH
import ui.Styles

val textInputWidth = 50.0

class HistogramPanelView : View() {
    override val root = vbox {
        squeezebox {
            fillHeight = false

            fold("RGB Adjustment", expanded = false) {
                vbox {
                    addClass(Styles.boxWithSpacing)

                    imageview(HistogramChartsForFilm.colorHistogramView)
                    colorchannelslider(HistogramEqualizationProperties.redChannelAdjustment, "Cyan", "Red")
                    colorchannelslider(HistogramEqualizationProperties.greenChannelAdjustment, "Magenta", "Green")
                    colorchannelslider(HistogramEqualizationProperties.blueChannelAdjustment, "Yellow", "Blue")

                    HistogramChartsForFilm.colorHistogramView.addListener { obs: ObservableValue<out Image>?, oldValue: Image?, newValue: Image? ->
                        if (oldValue == null && newValue != null) {
                            isExpanded = true
                        }
                        if (oldValue != null && newValue == null) {
                            isExpanded = false
                        }
                    }
                }
            }
            fold("Levels Adjustment", expanded = false) {
                vbox {
                    addClass(Styles.boxWithSpacing)

                    imageview(HistogramChartsForFilm.greyHistogramView)
                    hbox {
                        alignment = Pos.CENTER

                        // TODO make it nicer
                        val slider = RangeSlider()
                        slider.lowValueProperty().bindBidirectional(HistogramEqualizationProperties.lowLumLevel)
                        slider.highValueProperty().bindBidirectional(HistogramEqualizationProperties.highLumLevel)
                        slider.min = 0.0
                        slider.max = 1.0
                        slider.hgrow = Priority.ALWAYS
                        slider.padding = Insets(0.0, 5.0, 0.0, 2.0)

                        slider.lowValueChangingProperty().addListener { _, old, new ->
                            if (new && old != new && HistogramEqualizationProperties.highLightMaskEnabled()) {
                                HistogramEqualizationProperties.enableHighlightsMask.set(false)
                                HistogramEqualizationProperties.enableShadowsMask.set(true)
                            }
                        }

                        slider.highValueChangingProperty().addListener { _, old, new ->
                            if (new && old != new && HistogramEqualizationProperties.shadowsMaskEnabled()) {
                                HistogramEqualizationProperties.enableShadowsMask.set(false)
                                HistogramEqualizationProperties.enableHighlightsMask.set(true)
                            }
                        }

                        textfield {
                            maxWidth = textInputWidth
                            bind(HistogramEqualizationProperties.lowLumLevel, false, NumberStringConverter())
                        }
                        add(slider)
                        textfield {
                            maxWidth = textInputWidth
                            bind(HistogramEqualizationProperties.highLumLevel, false, NumberStringConverter())
                        }
                    }
                    hbox {
                        togglebutton("S") {
                            graphic = FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE)

                            selectedProperty().bindBidirectional(HistogramEqualizationProperties.enableShadowsMask)
                            action {
                                HistogramEqualizationProperties.enableHighlightsMask.set(false)
                            }
                        }
                        togglebutton("H") {
                            graphic = FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE)
                            alignment = Pos.BOTTOM_RIGHT

                            selectedProperty().bindBidirectional(HistogramEqualizationProperties.enableHighlightsMask)
                            action {
                                HistogramEqualizationProperties.enableShadowsMask.set(false)
                            }
                        }
                    }

                    HistogramChartsForFilm.greyHistogramView.addListener { obs: ObservableValue<out Image>?, oldValue: Image?, newValue: Image? ->
                        if (oldValue == null && newValue != null) {
                            isExpanded = true
                        }
                        if (oldValue != null && newValue == null) {
                            isExpanded = false
                        }
                    }
                }
            }
        }
    }
}

fun Pane.colorchannelslider(
    channelProperty: SimpleObjectProperty<Number>,
    leftLabel: String,
    rightLabel: String,
    op: VBox.() -> Unit = {}
) {
    val node = vbox {
        hbox {
            maxWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH - textInputWidth

            label(leftLabel) {
                useMaxWidth = true
                hgrow = Priority.ALWAYS
                textFill = c(leftLabel).darker()
            }
            label(rightLabel) {
                textFill = c(rightLabel)
            }
        }
        hbox {
            alignment = Pos.CENTER

            slider(-50.0, 50.0) {
                useMaxWidth = true
                prefWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH - textInputWidth

                valueProperty().bindBidirectional(channelProperty) // Todo inc by 1
            }
            textfield {
                maxWidth = textInputWidth
                bind(channelProperty, false, NumberStringConverter())
            }
        }
    }
    add(node)
    node.op()
}