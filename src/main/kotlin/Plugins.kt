import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


class StartPlugin : Plugin<Unit> {
    private val _result = MutableStateFlow<PluginResult>(PluginResult.Empty)
    override val result: Flow<PluginResult> = _result

    override suspend fun invoke(argument: Unit) {
        println("START running StartPlugin argument $argument")
        delay(100)
        _result.update {
            PluginResult.Value<StartPluginResult>(
                value = StartPluginResult()
            )
        }
    }

    data class StartPluginResult(
        val environment: String = "DEV"
    )
}

class AuthPlugin : Plugin<AuthPlugin.AuthPluginArgument> {
    private val _result = MutableStateFlow<PluginResult>(PluginResult.Empty)
    override val result: Flow<PluginResult> = _result

    override suspend fun invoke(argument: AuthPluginArgument) {
        println("START running AuthPlugin argument $argument")
        delay(200)
        _result.update {
            PluginResult.Value<AuthPluginResult>(
                value = AuthPluginResult(
                    value = "AuthPluginResult SUCCESS! env:${argument.environment}"
                )
            )
        }
    }

    data class AuthPluginArgument(
        val environment: String
    )

    data class AuthPluginResult(
        val value: String
    )
}

class SyncPlugin : Plugin<SyncPlugin.SyncPluginArgument> {
    private val _result = MutableStateFlow<PluginResult>(PluginResult.Empty)
    override val result: Flow<PluginResult> = _result

    override suspend fun invoke(argument: SyncPluginArgument) {
        println("START running SyncPlugin argument $argument")
        delay(300)
        _result.update {
            PluginResult.Value<SyncPluginResult>(
                value = SyncPluginResult()
            )
        }
    }

    data class SyncPluginArgument(
        val value: String
    )

    data class SyncPluginResult(
        val value: String = "SyncPluginResult SUCCESS!"
    )
}

class ValidationPlugin : Plugin<ValidationPlugin.ValidationPluginArgument> {
    private val _result = MutableStateFlow<PluginResult>(PluginResult.Empty)
    override val result: Flow<PluginResult> = _result

    override suspend fun invoke(argument: ValidationPluginArgument) {
        println("START running ValidationPlugin argument $argument")
        delay(300)
        _result.update {
            PluginResult.Value<ValidationPluginResult>(
                value = ValidationPluginResult()
            )
        }
    }

    data class ValidationPluginArgument(
        val value: String
    )

    data class ValidationPluginResult(
        val value: String = "ValidationPluginResult SUCCESS!"
    )
}