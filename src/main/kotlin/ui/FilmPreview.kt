package ui

import darkroom.Darkroom
import darkroom.toFxImage
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

object FilmPreview : SimpleObjectProperty<Image>() {
    private val timerTask: TimerTask

    private var currentFrame: BufferedImage? = null
        set(newValue) {
            field = newValue
            set(newValue?.toFxImage())
        }


    init {
        timerTask = Timer(true).scheduleAtFixedRate(500, 500) {
            val newFrame = getPreviewFrame()
            if (currentFrame != newFrame) {
                currentFrame = newFrame
            }
        }
    }

    fun dispose() {
        timerTask.cancel()
    }

    private fun getPreviewFrame(): BufferedImage? {
        if (SettingsPanelProperties.saveInProgress.value) {
            return null
        }

        return Darkroom.makeTestPrint()
    }
}