package ui.histograms

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty

object HistogramEqualizationProperties {
    val redChannelAdjustment = SimpleObjectProperty<Number>()
    val greenChannelAdjustment = SimpleObjectProperty<Number>()
    val blueChannelAdjustment = SimpleObjectProperty<Number>()

    val lowLumLevel = SimpleDoubleProperty()
    val highLumLevel = SimpleDoubleProperty()

    val enableShadowsMask = SimpleBooleanProperty(false)
    val enableHighlightsMask = SimpleBooleanProperty(false)

    init {
        setInitialValues()
    }

    fun resetAll() {
        setInitialValues()
    }

    fun highLightMaskEnabled(): Boolean {
        return enableHighlightsMask.value
    }

    fun shadowsMaskEnabled(): Boolean {
        return enableShadowsMask.value
    }

    private fun setInitialValues() {
        redChannelAdjustment.value = 0
        greenChannelAdjustment.value = 0
        blueChannelAdjustment.value = 0
        lowLumLevel.value = 0.0
        highLumLevel.value = 1.0
    }
}