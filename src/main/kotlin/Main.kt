import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val zapLib: ZapLib = ZapLib {
        environment = "PROD"
        onFeatureComponentCreatedCallback = { featureComponent ->
            println("FEATURE COMPONENT [$featureComponent]")
        }
    }.start()

    println("Successfully launched ZAP-LIB!!!")
}