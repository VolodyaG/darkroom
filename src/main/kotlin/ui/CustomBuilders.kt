package ui

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.control.Slider
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import ui.histograms.textInputWidth

fun Pane.settingsslider(name: String, property: SimpleDoubleProperty, op: Slider.() -> Unit = {}) {
    add(
        gridpane {
            row {
                label(name) {
                    addClass(Styles.propertyLabel)
                }
                gridpaneConstraints {
                    columnSpan = 2
                }
            }
            row {
                val slider = slider {
                    isShowTickLabels = true
                    isShowTickMarks = true
                    isSnapToTicks = true
                    useMaxWidth = true

                    valueProperty().bindBidirectional(property)
                }
                textfield {
                    bind(property, false, NumberStringConverter())
                }

                slider.op()
            }
            columnConstraints.addAll(
                ColumnConstraints(
                    0.0, LEFT_AND_RIGHT_WINDOWS_WIDTH - 50.0, Double.MAX_VALUE,
                    Priority.ALWAYS, HPos.LEFT, true
                ),
                ColumnConstraints(50.0, 50.0, 50.0, Priority.NEVER, HPos.LEFT, true)
            )
        }
    )
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