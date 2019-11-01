package ui

import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import ui.converters.FilmTypeStringConverter
import ui.histograms.HistogramEqualizationProperties

class SettingsPanelView : View() {
    private val toggleGroup = ToggleGroup()

    override val root = vbox {
        addClass(Styles.boxWithSpacing)

        squeezebox {
            fillHeight = false

            fold("General", expanded = true) {
                vbox {
                    addClass(Styles.boxWithSpacing)

                    label("Save to") {
                        addClass(Styles.propertyLabel)
                    }
                    hbox {
                        alignment = Pos.CENTER_LEFT

                        addClass(Styles.boxWithSpacing)

                        label(SettingsPanelProperties.printsFolder)
                        button {
                            useMaxWidth = true
                            graphic = FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT)

                            setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE)

                            action {
                                chooseDirectory()
                            }
                        }
                    }
                    label("Mode") {
                        addClass(Styles.propertyLabel)
                    }
                    hbox {
                        alignment = Pos.CENTER

                        addClass(Styles.boxWithSpacing)

                        radiobutton(FilmTypes.BLACK_AND_WHITE.displayName, toggleGroup)
                        radiobutton(FilmTypes.COLOR_NEGATIVE.displayName, toggleGroup)
                        radiobutton(FilmTypes.POSITIVE.displayName, toggleGroup)
                    }
                }
            }
            fold("Crop and Rotate", expanded = true) {
                vbox {
                    addClass(Styles.boxWithSpacing)

                    settingsslider("Rotation Angle", SettingsPanelProperties.rotation) {
                        min = -180.0
                        max = 180.0
                        blockIncrement = 90.0
                        majorTickUnit = 90.0

                        padding = Insets(0.0, 5.0, 0.0, 5.0)
                    }
                    label("Crop") {
                        addClass(Styles.propertyLabel)
                    }
                    hbox {
                        addClass(Styles.boxWithSpacing)

                        togglebutton("Show crop area") {
                            graphic = FontAwesomeIconView(FontAwesomeIcon.CROP)
                            selectedProperty().bindBidirectional(SettingsPanelProperties.isCropVisible)
                        }
                        button("Reset to default") {
                            action {
                                SettingsPanelProperties.resetCropArea()
                            }
                        }
                        label {
                            graphic = FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE)
                            alignment = Pos.CENTER_RIGHT
                            hgrow = Priority.ALWAYS
                            useMaxSize = true

                            style {
                                fontSize = 16.px
                            }

                            onHover {
                                // TODO show tooltip
                            }
                        }
                    }
                }
            }
            fold("Adjust Color", expanded = true) {
                vbox {
                    addClass(Styles.boxWithSpacing)

                    settingsslider("Contrast", SettingsPanelProperties.contrast) {
                        min = -1.0
                        max = 1.0
                        blockIncrement = 0.1
                        majorTickUnit = 0.5
                    }
                    settingsslider("Brightness", SettingsPanelProperties.brightness) {
                        min = -1.0
                        max = 1.0
                        blockIncrement = 0.1
                        majorTickUnit = 0.5
                    }
                }
            }
        }
        hbox {
            vgrow = Priority.ALWAYS
            alignment = Pos.BOTTOM_RIGHT

            addClass(Styles.boxWithSpacing)

            button("Reset all") {
                action {
                    SettingsPanelProperties.resetAll()
                    HistogramEqualizationProperties.resetAll()
                }
            }
        }
    }

    init {
        Bindings.bindBidirectional(
            toggleGroup.selectedValueProperty<String>(),
            SettingsPanelProperties.filmType,
            FilmTypeStringConverter()
        )
    }

    private fun chooseDirectory() {
        val chosenDirectory = chooseDirectory {
            title = "Choose prints folder"
        }
        if (chosenDirectory != null) {
            SettingsPanelProperties.changePrintsLocation(chosenDirectory)
        }
    }
}

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
