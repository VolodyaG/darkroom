package driver

import com.github.sarxos.webcam.WebcamDevice
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver
import com.github.sarxos.webcam.util.NixVideoDevUtils
import java.util.*

class FilmScannerDriver : V4l4jDriver() {
    override fun getDevices(): MutableList<WebcamDevice> {
        val devices = ArrayList<WebcamDevice>()
        val vfiles = NixVideoDevUtils.getVideoFiles()

        for (vfile in vfiles) {
            devices.add(FilmScannerDevice(vfile))
        }

        return devices
    }
}