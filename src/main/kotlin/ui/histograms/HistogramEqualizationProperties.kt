package ui.histograms

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty

object HistogramEqualizationProperties {
    val applyColorsAdjustment = SimpleBooleanProperty()

    val redChannelAdjustment = SimpleObjectProperty<Number>()
    val greenChannelAdjustment = SimpleObjectProperty<Number>()
    val blueChannelAdjustment = SimpleObjectProperty<Number>()

    val applyLevelsAdjustment = SimpleBooleanProperty()

    val lowLumLevel = SimpleDoubleProperty()
    val highLumLevel = SimpleDoubleProperty()

    val enableShadowsMask = SimpleBooleanProperty()
    val enableHighlightsMask = SimpleBooleanProperty()

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
        applyColorsAdjustment.value = true
        redChannelAdjustment.value = 0
        greenChannelAdjustment.value = 0
        blueChannelAdjustment.value = 0
        applyLevelsAdjustment.value = true
        lowLumLevel.value = 0.0
        highLumLevel.value = 1.0
        enableShadowsMask.value = false
        enableHighlightsMask.value = false
    }
}