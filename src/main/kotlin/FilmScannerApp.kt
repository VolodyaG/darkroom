import com.guigarage.flatterfx.FlatterFX
import javafx.application.Application
import tornadofx.App
import ui.MainView
import ui.Styles

class MyApp: App(MainView::class, Styles::class)

fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}