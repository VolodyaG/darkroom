import darkroom.FilmScanner
import javafx.application.Application
import javafx.stage.Stage
import tornadofx.App
import ui.*

class MyApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        with(stage) {
            minWidth = FILM_PREVIEW_WINDOW_WIDTH + LEFT_AND_RIGHT_WINDOWS_WIDTH * 1.71
            minHeight = FILM_PREVIEW_WINDOW_HEIGHT + 90
            width = minWidth
            height = minHeight

            super.start(this)

            stage.setOnCloseRequest {
                FilmPreview.dispose()
                FilmScanner.dispose()
            }
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(MyApp::class.java, *args)
}

fun String.isEnvTrue(): Boolean {
    return System.getenv(this) == true.toString()
}
