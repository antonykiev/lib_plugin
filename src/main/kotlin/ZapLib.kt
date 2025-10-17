

class ZapLib(
    val environment: String
) {
    suspend fun start(): ZapLib = this.apply { initZapLib() }
}