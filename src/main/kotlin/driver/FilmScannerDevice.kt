package driver

import au.edu.jcu.v4l4j.ImageFormat
import au.edu.jcu.v4l4j.VideoDevice
import com.github.sarxos.webcam.WebcamException
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDevice
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


// To access parent class private fields, we need to turn to java reflections
val disposedField = V4l4jDevice::class.java.getDeclaredField("disposed")
val openField = V4l4jDevice::class.java.getDeclaredField("open")
val videoDeviceField = V4l4jDevice::class.java.getDeclaredField("videoDevice")
val grabberField = V4l4jDevice::class.java.getDeclaredField("grabber")
val videoBestImageFormatField = V4l4jDevice::class.java.getDeclaredField("videoBestImageFormat")

class FilmScannerDevice(file: File?) : V4l4jDevice(file) {
    init {
        disposedField.isAccessible = true
        openField.isAccessible = true
        videoDeviceField.isAccessible = true
        grabberField.isAccessible = true
        videoBestImageFormatField.isAccessible = true
    }

    private val disposed: AtomicBoolean = disposedField.get(this) as AtomicBoolean
    private val open: AtomicBoolean = openField.get(this) as AtomicBoolean
    private val videoDevice: VideoDevice = videoDeviceField.get(this) as VideoDevice
    private val imageFormat: ImageFormat? = grabberField.get(this) as ImageFormat?

    override fun open() {
        if (disposed.get()) {
            throw WebcamException("Cannot open device because it has been already disposed")
        }

        if (!open.compareAndSet(false, true)) {
            return
        }

        val grabber = videoDevice.getJPEGFrameGrabber(resolution.width, resolution.height, 0, 0, 95, imageFormat)
        grabberField.set(this, grabber)

        grabber.setCaptureCallback(this)
        grabber.startCapture()
    }
}