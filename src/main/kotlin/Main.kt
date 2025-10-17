import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val zapLib: ZapLib = ZapLib {
        environment = "PROD"
    }.start()

    println("Successfully launched ZAP-LIB!!!")
}