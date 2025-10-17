

suspend fun ZapLib(block: ZapLibBuilder.() -> Unit): ZapLib {
    return ZapLibBuilder().apply(block).start()
}

class ZapLibBuilder {
    var environment = "DEV"

    suspend fun start(): ZapLib {
        return ZapLib(
            environment = environment
        ).start()
    }
}