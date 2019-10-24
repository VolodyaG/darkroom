import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import tornadofx.App
import ui.MainView
import ui.Styles
import java.awt.image.BufferedImage

class MyApp: App(MainView::class, Styles::class)

fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}

fun BufferedImage.toFxImage(): Image {
    return SwingFXUtils.toFXImage(this, null)
}