

class ZapLib(
    val environment: String
) {
    fun start(): ZapLib = this.apply { initZapLib() }
}