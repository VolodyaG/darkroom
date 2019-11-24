package ui

import darkroom.Darkroom
import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import ui.converters.FilmTypeStringConverter
import ui.histograms.HistogramEqualizationProperties

class SettingsPanelView : View() {
    private val toggleGroup = ToggleGroup()

    override val root = vbox {
        hbox {
            addClass(Styles.boxWithSpacing)
            addClass(Styles.centeredAlignment)
            addClass(Styles.filmTypeContainer)

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
        val squeezeBox = squeezebox {
            fillHeight = false

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
                        min = -64.0
                        max = 64.0
                        minorTickCount = 15
                        majorTickUnit = 32.0
                        blockIncrement = 1.0
                        isSnapToTicks = true
                    }
                    settingsslider("Brightness", SettingsPanelProperties.brightness, FontAwesomeIcon.SUN_ALT) {
                        min = -64.0
                        max = 64.0
                        minorTickCount = 15
                        majorTickUnit = 32.0
                        blockIncrement = 1.0
                        isSnapToTicks = true
                    }
                }
            }
            foldwithprogress("Save folder", false, SettingsPanelProperties.previewLoadInProgress) {
                hbox {
                    addClass(Styles.boxWithSpacing)

                    useMaxWidth = true
                    alignment = Pos.CENTER_LEFT

                    label(SettingsPanelProperties.printsFolder) {
                        textOverrun = OverrunStyle.LEADING_ELLIPSIS
                        useMaxWidth = true
                        hgrow = Priority.ALWAYS
                    }
                    button {
                        graphic = FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN_ALT)

                        setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE)

                        action {
                            chooseDirectory()
                        }
                    }
                }
            }

            VBox.setMargin(this, Insets(10.0, 0.0, 0.0, 0.0))
        }
        val galleryScrollPane = scrollpane {
            addClass(Styles.galleryScrollContainer)

            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vgrow = Priority.ALWAYS

            tilepane {
                addClass(Styles.gallery)

                isFitToWidth = true

                children.bind(SettingsPanelProperties.previewImages) {
                    return@bind imageview(it) {
                        fitWidth = 80.0
                        fitHeight = 60.0
                        isPreserveRatio = true
                    }
                }
            }

            visibleProperty().bind((squeezeBox.getChildList()?.last() as TitledPane).expandedProperty())
            visibleProperty().onChange { visible ->
                if (visible) {
                    runAsync {
                        SettingsPanelProperties.loadPrintsFolderContents()
                    }
                }
            }

            VBox.setMargin(this, Insets(0.0, 0.0, 5.0, 0.0))
        }
        hbox {
            addClass(Styles.boxWithSpacing)

            button("Scan") {
                addClass(Styles.scanButton)

                useMaxWidth = true
                hgrow = Priority.ALWAYS

                action {
                    runAsync {
                        isDisable = true
                        Darkroom.printImage()

                        if (galleryScrollPane.isVisible) {
                            SettingsPanelProperties.loadLastFromPrintsFolder()
                        }

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
