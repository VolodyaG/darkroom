package ui.histograms

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.HBox
import javafx.util.converter.NumberStringConverter
import org.controlsfx.control.RangeSlider
import tornadofx.*

class HistogramPanelView : View() {
    override val root = vbox {
        spacing = 5.0
        prefWidth = 400.0

        imageview(HistogramChartsForFilm.colorHistogramView)

        add(createSliderForColorChannel(HistogramEqualizationProperties.redChannelAdjustment, "Red"))
        add(createSliderForColorChannel(HistogramEqualizationProperties.greenChannelAdjustment, "Green"))
        add(createSliderForColorChannel(HistogramEqualizationProperties.blueChannelAdjustment, "Blue"))

        imageview(HistogramChartsForFilm.greyHistogramView)
        add(RangeSlider(0.0, 255.0, 0.0, 255.0))
        slider()
    }

    private fun createSliderForColorChannel(channelProperty: SimpleObjectProperty<Number>, label: String): HBox {
        return hbox {
            label(label) {
                prefWidth = 40.0
            }
            val colorChannelSlider = slider(-50.0, 50.0) {
                useMaxWidth = true
                prefWidth = 310.0
                // Todo inc by 1
            }
            colorChannelSlider.valueProperty().bindBidirectional(channelProperty)
            val input = textfield {
                maxWidth = 50.0
            }
            input.bind(colorChannelSlider.valueProperty(), false, NumberStringConverter())
        }
    }

}