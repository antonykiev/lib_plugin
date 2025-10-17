

class ZapLib(
    val environment: String,
    val onFeatureComponentCreated: (FeatureComponent) -> Unit
) {
    suspend fun start(): ZapLib = this.apply { initZapLib() }
}