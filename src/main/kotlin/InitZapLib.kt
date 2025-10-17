import kotlinx.coroutines.runBlocking


suspend fun ZapLib.initZapLib() {
    ZapMediator(
        startPlugin = StartPlugin(),
        authPlugin = AuthPlugin(),
        syncPlugin = SyncPlugin(),
        validationPlugin = ValidationPlugin(),
        componentCreateListener = object : ComponentCreateListener {
            override fun onFeatureComponentCreated(component: FeatureComponent) {
                this@initZapLib.onFeatureComponentCreated(component)
            }
        },
    ).invoke()
}