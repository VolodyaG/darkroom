package ui

import darkroom.Darkroom
import darkroom.FilmTypes
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*
import ui.converters.FilmTypeStringConverter
import ui.histograms.HistogramEqualizationProperties
import ui.selection.EdgeDetectionService

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
                        disableProperty().bind(SettingsPanelProperties.edgeDetectionInProgress)

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

                        val cropButton = button("Find edges") {
                            useMaxWidth = true
                            hgrow = Priority.ALWAYS

                            action {
                                val imageView = this.scene.lookup(".mainImageView") as ImageView

                                runAsync {
                                    SettingsPanelProperties.isCropVisible.set(false)
                                    SettingsPanelProperties.edgeDetectionInProgress.set(true)

                                    val rectangle = EdgeDetectionService.getRectangle(
                                        imageView, SettingsPanelProperties.rotation.value
                                    )
                                    SettingsPanelProperties.cropArea.set(rectangle)

                                    SettingsPanelProperties.isCropVisible.set(true)
                                    SettingsPanelProperties.edgeDetectionInProgress.set(false)
                                }
                            }
                        }
                        progressindicator {
                            progress = ProgressIndicator.INDETERMINATE_PROGRESS

                            fitToHeight(cropButton)
                            visibleProperty().bind(SettingsPanelProperties.edgeDetectionInProgress)
                        }
                        label {
                            addClass(Styles.infoIcon)

                            graphic = FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE)
                            alignment = Pos.CENTER_RIGHT
                            useMaxSize = true

                            tooltip(
                                "* Press the button to find crop area automatically\n" +
                                        "* Drag mouse from corner to corner to draw crop area manually\n" +
                                        "* Press ESC to dismiss crop area"
                            )
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
            fold("Save folder", false) {
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
                disableProperty().bind(SettingsPanelProperties.previewLoadInProgress)
            }

            VBox.setMargin(this, Insets(10.0, 0.0, 0.0, 0.0))
        }
        stackpane {
            vgrow = Priority.ALWAYS

            scrollpane {
                addClass(Styles.galleryScrollContainer)

                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER

                tilepane {
                    addClass(Styles.gallery)

                    isFitToWidth = true

                    children.bind(SettingsPanelProperties.previewImages) {
                        imageview(it) {
                            isPreserveRatio = true
                        }
                    }
                }

                disableProperty().bind(SettingsPanelProperties.previewLoadInProgress)
            }
            progressindicator {
                addClass(Styles.galleryLoadProgress)

                progress = ProgressIndicator.INDETERMINATE_PROGRESS
                alignment = Pos.CENTER

                visibleProperty().bind(SettingsPanelProperties.previewLoadInProgress)
            }

            visibleProperty().bind((squeezeBox.getChildList()?.last() as TitledPane).expandedProperty())
            visibleProperty().onChange { visible ->
                if (visible) {
                    runAsync {
                        SettingsPanelProperties.loadPrintsFolderContents()
                    }
                } else {
                    SettingsPanelProperties.previewImages.clear()
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
                        isDisable = false

                        if ((squeezeBox.getChildList()?.last() as TitledPane).isExpanded) {
                            SettingsPanelProperties.loadLastFromPrintsFolder()
                        }
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
