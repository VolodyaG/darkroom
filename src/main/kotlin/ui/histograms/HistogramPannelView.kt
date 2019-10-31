package ui.histograms

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import org.controlsfx.control.RangeSlider
import tornadofx.*
import ui.LEFT_AND_RIGHT_WINDOWS_WIDTH
import ui.SettingsPannelProperties

val textInputWidth = 50.0

class HistogramPanelView : View() {
    override val root = vbox {
        spacing = 5.0

        imageview(HistogramChartsForFilm.colorHistogramView)

        colorchannelslider(HistogramEqualizationProperties.redChannelAdjustment, "Cyan", "Red")
        colorchannelslider(HistogramEqualizationProperties.greenChannelAdjustment, "Magenta", "Green")
        colorchannelslider(HistogramEqualizationProperties.blueChannelAdjustment, "Yellow", "Blue")

        imageview(HistogramChartsForFilm.greyHistogramView)

        hbox {// TODO make it nicer
            val slider = RangeSlider()
            slider.lowValueProperty().bindBidirectional(HistogramEqualizationProperties.lowLumLevel)
            slider.highValueProperty().bindBidirectional(HistogramEqualizationProperties.highLumLevel)
            slider.min = 0.0
            slider.max = 1.0
            slider.hgrow = Priority.ALWAYS
            slider.padding = Insets(0.0, 5.0, 0.0, 2.0)

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
