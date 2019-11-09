package ui

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.HPos
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.TitledPane
import javafx.scene.layout.*
import javafx.util.converter.NumberStringConverter
import org.controlsfx.control.RangeSlider
import org.controlsfx.control.ToggleSwitch
import tornadofx.*
import ui.histograms.textInputWidth

fun Pane.settingsslider(
    name: String,
    property: SimpleDoubleProperty,
    icon: FontAwesomeIcon? = null,
    op: Slider.() -> Unit = {}
) {
    add(
        gridpane {
            row {
                label(name) {
                    addClass(Styles.settingNameLabel)

                    if (icon != null) {
                        val iconView = FontAwesomeIconView(icon)
                        iconView.fillProperty().bind(textFillProperty())

                        graphic = iconView
                    }
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
            addClass(Styles.centeredAlignment)

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

fun Pane.rangeslider(min: Double, max: Double, op: RangeSlider.() -> Unit = {}) {
    val slider = RangeSlider()

    slider.min = min
    slider.max = max
    slider.op()

    add(slider)
}

fun SqueezeBox.foldwithtoggle(
    title: String,
    expanded: Boolean = false,
    toggleProperty: SimpleBooleanProperty? = null,
    op: TitledPane.() -> Unit = {}
) {
    val label = Label(title)
    val switch = ToggleSwitch()
    val gridPane = GridPane()

    switch.selectedProperty().bindBidirectional(toggleProperty)
    gridPane.addRow(0, label, switch)

    val columnConstraints = gridPane.constraintsForColumn(1)
    columnConstraints.hgrow = Priority.ALWAYS
    columnConstraints.halignment = HPos.RIGHT
    columnConstraints.isFillWidth = true

    val fold = fold(expanded = expanded) {
        graphic = gridPane
    }
    op.invoke(fold)
}