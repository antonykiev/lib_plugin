import kotlinx.coroutines.runBlocking

object ZapLibImpl : ZapLibConstructor

fun main() = runBlocking {
    println("Successfully launched ZAP-LIB!!!")

    val feature = ZapLibImpl.featureComponent()
    println("featureComponent() generated and invoked: $feature")
}