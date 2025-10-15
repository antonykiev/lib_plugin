

suspend fun ZapLib(block: ZapLibBuilder.() -> Unit): ZapLib {
    return ZapLibBuilder().apply(block).start()
}

class ZapLibBuilder {
    var environment = "DEV"

    fun start(): ZapLib {
        return ZapLib(
            environment = environment
        ).start()
    }
}