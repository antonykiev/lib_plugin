import AuthPlugin.AuthPluginArgument
import SyncPlugin.SyncPluginArgument
import ValidationPlugin.ValidationPluginArgument
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine


class ZapMediator(
    private val startPlugin: StartPlugin,
    private val authPlugin: AuthPlugin,
    private val syncPlugin: SyncPlugin,
    private val validationPlugin: ValidationPlugin,
) {

    suspend operator fun invoke() {
        combine(
            startPlugin.result,
            authPlugin.result,
            syncPlugin.result,
            validationPlugin.result,
        ) { startPluginResult: PluginResult, authPluginResult, syncPluginResult, validationPluginResult ->
            val appState = mapToAppState(
                startPluginResult = startPluginResult,
                authPluginResult = authPluginResult,
                syncPluginResult = syncPluginResult,
                validationPluginResult = validationPluginResult
            )

            when (appState) {
                AppState.NotStarted -> {
                    startPlugin.invoke(Unit)
                }

                AppState.StartPluginDone -> {
                    authPlugin.invoke(
                        argument = AuthPluginArgument(
                            (startPluginResult as PluginResult.Value<StartPlugin.StartPluginResult>).value.environment
                        )
                    )

                    syncPlugin.invoke(
                        argument = SyncPluginArgument(
                            (startPluginResult as PluginResult.Value<StartPlugin.StartPluginResult>).value.environment
                        )
                    )
                }

                AppState.AuthAndSyncPluginDone -> {
                    validationPlugin.invoke(
                        argument = ValidationPluginArgument(
                            value = (authPluginResult as PluginResult.Value<AuthPlugin.AuthPluginResult>).value.value +
                                    (syncPluginResult as PluginResult.Value<SyncPlugin.SyncPluginResult>).value.value
                        )
                    )
                }

                AppState.ValidatePluginDone -> {
                    println("Mediator has finished work!!!")
                }

                AppState.Unexpected -> {
                    println("--------------ERROR--------------")
                    println(startPluginResult)
                    println(authPluginResult)
                    println(syncPluginResult)
                    println(validationPluginResult)
                    println("--------------ERROR--------------")
                }

                AppState.Waiting -> {

                }
            }
        }.collect()
    }

    private fun mapToAppState(
        startPluginResult: PluginResult,
        authPluginResult: PluginResult,
        syncPluginResult: PluginResult,
        validationPluginResult: PluginResult
    ): AppState {
        if (
            startPluginResult is PluginResult.Empty &&
            authPluginResult is PluginResult.Empty &&
            syncPluginResult is PluginResult.Empty &&
            validationPluginResult is PluginResult.Empty
        ) return AppState.NotStarted

        if (
            startPluginResult !is PluginResult.Empty &&
            authPluginResult is PluginResult.Empty &&
            syncPluginResult is PluginResult.Empty &&
            validationPluginResult is PluginResult.Empty
        ) return AppState.StartPluginDone

        if (
            startPluginResult !is PluginResult.Empty &&
            authPluginResult !is PluginResult.Empty &&
            syncPluginResult is PluginResult.Empty &&
            validationPluginResult is PluginResult.Empty
        ) return AppState.Waiting

        if (
            startPluginResult !is PluginResult.Empty &&
            authPluginResult !is PluginResult.Empty &&
            syncPluginResult !is PluginResult.Empty &&
            validationPluginResult is PluginResult.Empty
        ) return AppState.AuthAndSyncPluginDone

        if (
            startPluginResult !is PluginResult.Empty &&
            authPluginResult !is PluginResult.Empty &&
            syncPluginResult !is PluginResult.Empty &&
            validationPluginResult !is PluginResult.Empty
        ) return AppState.ValidatePluginDone

        return AppState.Unexpected
    }


    sealed interface AppState {
        data object NotStarted : AppState
        data object StartPluginDone : AppState
        data object AuthAndSyncPluginDone : AppState
        data object ValidatePluginDone : AppState
        data object Waiting : AppState
        data object Unexpected : AppState
    }
}