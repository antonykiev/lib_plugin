import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val zapLib = ZapLib {
        environment = "PROD"
    }
    println("Successfully launched ZAP-LIB!!!")
}