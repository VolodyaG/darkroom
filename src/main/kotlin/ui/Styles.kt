package ui

import com.guigarage.flatterfx.FlatterFX
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles: Stylesheet() {
    init {
        FlatterFX.style()

        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }

        button {
            fontSize = 16.px
        }
    }
}