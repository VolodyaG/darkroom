import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import ui.*
import java.awt.image.BufferedImage

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            minWidth = FILM_PREVIEW_WINDOW_WIDTH + LEFT_AND_RIGHT_WINDOWS_WIDTH * 1.7
            minHeight = FILM_PREVIEW_WINDOW_HEIGHT + 85
            width = minWidth
            height = minHeight

            super.start(this)
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}

fun BufferedImage.toFxImage(): Image {
    return SwingFXUtils.toFXImage(this, null)
}

/*
* ToDo features list
* Bypass button
* Preview/Processing
* Grayscale gradient
* Presets?
*
* */