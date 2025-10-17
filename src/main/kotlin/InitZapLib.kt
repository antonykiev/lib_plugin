import kotlinx.coroutines.runBlocking


suspend fun ZapLib.initZapLib() {
    ZapMediator(
        startPlugin = StartPlugin(),
        authPlugin = AuthPlugin(),
        syncPlugin = SyncPlugin(),
        validationPlugin = ValidationPlugin()
    ).invoke()
}