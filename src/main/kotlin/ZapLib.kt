

class ZapLib(
    val environment: String
) {

    fun start(): ZapLib {
        initZapLib()
        return this
    }
}