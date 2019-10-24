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

        add(createSliderForColorChannel(HistogramEqualizationProperties.redChannelAdjustment))
        add(createSliderForColorChannel(HistogramEqualizationProperties.blueChannelAdjustment))
        add(createSliderForColorChannel(HistogramEqualizationProperties.greenChannelAdjustment))

        imageview(HistogramChartsForFilm.greyHistogramView)
        add(RangeSlider(0.0, 255.0, 0.0, 255.0))
        slider()
    }

    private fun createSliderForColorChannel(channelProperty: SimpleObjectProperty<Number>): HBox {
        return hbox {
            val colorChannelSlider = slider(-100.0, 100.0) {
                useMaxWidth = true
                prefWidth = 350.0
            }
            colorChannelSlider.valueProperty().bindBidirectional(channelProperty)
            val input = textfield {
                maxWidth = 50.0
            }
            input.bind(colorChannelSlider.valueProperty(), false, NumberStringConverter())
        }
    }

}