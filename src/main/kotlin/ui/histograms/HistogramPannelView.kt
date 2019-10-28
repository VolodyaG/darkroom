package ui.histograms

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import org.controlsfx.control.RangeSlider
import tornadofx.*
import ui.LEFT_AND_RIGHT_WINDOWS_WIDTH

class HistogramPanelView : View() {
    override val root = vbox {
        spacing = 5.0

        imageview(HistogramChartsForFilm.colorHistogramView)

        add(createSliderForColorChannel(HistogramEqualizationProperties.redChannelAdjustment, "Cyan", "Red"))
        add(createSliderForColorChannel(HistogramEqualizationProperties.greenChannelAdjustment, "Magenta", "Green"))
        add(createSliderForColorChannel(HistogramEqualizationProperties.blueChannelAdjustment, "Yellow", "Blue"))

        imageview(HistogramChartsForFilm.greyHistogramView)
        add(RangeSlider(0.0, 255.0, 0.0, 255.0))
        slider()
    }

    private fun createSliderForColorChannel(
        channelProperty: SimpleObjectProperty<Number>,
        leftLabel: String,
        rightLabel: String
    ): VBox {
        val textInputWidth = 50.0
        return vbox {
            hbox {
                label(leftLabel) {
                    textFill = c(leftLabel).darker()
                }
                label(rightLabel) {
                    textFill = c(rightLabel)
                }
            }
            hbox {
                val colorChannelSlider = slider(-50.0, 50.0) {
                    useMaxWidth = true
                    prefWidth = LEFT_AND_RIGHT_WINDOWS_WIDTH - textInputWidth
                    // Todo inc by 1
                }
                colorChannelSlider.valueProperty().bindBidirectional(channelProperty)
                val input = textfield {
                    maxWidth = textInputWidth
                }
                input.bind(colorChannelSlider.valueProperty(), false, NumberStringConverter())
            }
        }
    }

}