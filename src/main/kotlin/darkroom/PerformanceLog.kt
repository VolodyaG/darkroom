package darkroom

import isEnvTrue
import java.util.*

inline fun <T>performancelog(noinline op: () -> T): T {
    val startTime = Date().time
    val image = op()
    val endTime = Date().time

    if ("PERFORMANCE_DEBUG".isEnvTrue()) {
        val methodName = op.javaClass.enclosingMethod.name
        val title = methodName
            .split(Regex("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
            .joinToString(" ")
            .capitalize()

        println("${title.padEnd(50, ' ')} ${endTime - startTime}ms")
    }

    return image
}