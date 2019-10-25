package ui.histograms

import javafx.beans.property.SimpleObjectProperty

object HistogramEqualizationProperties {
    val redChannelAdjustment = SimpleObjectProperty<Number>(0)
    val greenChannelAdjustment = SimpleObjectProperty<Number>(0)
    val blueChannelAdjustment = SimpleObjectProperty<Number>(0)
}