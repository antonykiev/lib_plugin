import kotlinx.coroutines.flow.Flow


interface Plugin<A> {
    val result: Flow<PluginResult>
    suspend operator fun invoke(argument: A)
}

sealed interface PluginResult {
    data object Empty: PluginResult
    data class Value<R>(
        val value: R
    ): PluginResult
}