import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.App
import ui.MainView
import ui.Styles
import java.awt.image.BufferedImage

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            minWidth = 640.0 + 400.0 + 200.0
            minHeight = 585.0
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