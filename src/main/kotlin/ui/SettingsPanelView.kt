package ui

import darkroom.Darkroom
import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.OverrunStyle
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
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
                        addClass(Styles.settingNameLabel)
                    }
                    hbox {
                        addClass(Styles.boxWithSpacing)
                        addClass(Styles.centeredAlignment)

                        label(SettingsPanelProperties.printsFolder) {
                            textOverrun = OverrunStyle.LEADING_ELLIPSIS
                        }
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
                        addClass(Styles.settingNameLabel)
                    }
                    hbox {
                        addClass(Styles.boxWithSpacing)
                        addClass(Styles.centeredAlignment)

                        radiobutton(FilmTypes.BLACK_AND_WHITE.displayName, toggleGroup) {
                            prefWidthProperty().bind((parent as HBox).widthProperty().multiply(0.33))
                        }
                        radiobutton(FilmTypes.COLOR_NEGATIVE.displayName, toggleGroup) {
                            prefWidthProperty().bind((parent as HBox).widthProperty().multiply(0.33))
                        }
                        radiobutton(FilmTypes.POSITIVE.displayName, toggleGroup) {
                            prefWidthProperty().bind((parent as HBox).widthProperty().multiply(0.33))
                        }
                    }
                }
            }
            fold("Crop and Rotate", expanded = true) {
                vbox {
                    addClass(Styles.boxWithSpacing)

                    settingsslider("Rotate", SettingsPanelProperties.rotation, FontAwesomeIcon.ROTATE_RIGHT) {
                        addClass(Styles.rotateSlider)

                        min = -180.0
                        max = 180.0
                        minorTickCount = 0
                        majorTickUnit = 90.0
                        blockIncrement = 90.0
                        isSnapToTicks = true
                        isShowTickMarks = true
                    }
                    label("Crop") {
                        addClass(Styles.settingNameLabel)

                        val icon = FontAwesomeIconView(FontAwesomeIcon.CROP)
                        icon.fillProperty().bind(textFillProperty())

                        graphic = icon
                    }
                    hbox {
                        addClass(Styles.boxWithSpacing)

                        togglebutton("Show crop area") {
                            selectedProperty().bindBidirectional(SettingsPanelProperties.isCropVisible)
                        }
                        button("Reset to default") {
                            action {
                                SettingsPanelProperties.resetCropArea()
                            }
                        }
                        label {
                            addClass(Styles.infoIcon)

                            graphic = FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE)
                            alignment = Pos.CENTER_RIGHT
                            hgrow = Priority.ALWAYS
                            useMaxSize = true

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

                    settingsslider("Contrast", SettingsPanelProperties.contrast, FontAwesomeIcon.ADJUST) {
                        min = -1.0
                        max = 1.0
                        blockIncrement = 0.1
                        majorTickUnit = 0.5
                    }
                    settingsslider("Brightness", SettingsPanelProperties.brightness, FontAwesomeIcon.SUN_ALT) {
                        min = -1.0
                        max = 1.0
                        blockIncrement = 0.1
                        majorTickUnit = 0.5
                    }
                }
            }
        }
        hbox {
            addClass(Styles.boxWithSpacing)

            vgrow = Priority.ALWAYS
            alignment = Pos.BOTTOM_RIGHT

            button("Scan") {
                addClass(Styles.scanButton)

                useMaxWidth = true
                hgrow = Priority.ALWAYS

                action {
                    runAsync {
                        isDisable = true
                        Darkroom.printImage()
                        isDisable = false
                    }
                }
            }
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
