import AuthPlugin.AuthPluginArgument
import SyncPlugin.SyncPluginArgument
import ValidationPlugin.ValidationPluginArgument
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun ZapLib.initZapLib() = runBlocking {

    val startPlugin = StartPlugin()
    val authPlugin = AuthPlugin()
    val syncPlugin = SyncPlugin()
    val validationPlugin = ValidationPlugin()

    launch {
        startPlugin.invoke(Unit)
        startPlugin.result
            .filterIsInstance<PluginResult.Value<StartPlugin.StartPluginResult>>()
            .collect {
                authPlugin.invoke(AuthPluginArgument(it.value.environment))
                syncPlugin.invoke(SyncPluginArgument(it.value.environment))
            }
    }

    launch {
        combine(
            authPlugin.result
                .filterIsInstance<PluginResult.Value<AuthPlugin.AuthPluginResult>>(),
            syncPlugin.result
                .filterIsInstance<PluginResult.Value<SyncPlugin.SyncPluginResult>>(),
        ) { authPluginRes, syncPluginRes ->
            validationPlugin.invoke(
                ValidationPluginArgument("")
            )
        }.collect()
    }
}