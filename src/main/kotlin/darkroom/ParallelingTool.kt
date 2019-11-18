package darkroom

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.awt.image.BufferedImage

fun <T> splitAndRunAsync(splitter: ImageSplitter<T>, callback: (split: T) -> Unit): BufferedImage {
    splitter.split()

    runBlocking {
        splitter.splits
            .map { split -> GlobalScope.async { callback(split) } }
            .forEach { it.await() }
    }

    return splitter.join()
}
