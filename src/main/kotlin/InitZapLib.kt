import kotlinx.coroutines.runBlocking


fun ZapLib.initZapLib() = runBlocking {
    println("=== Plugin Tree System with Custom Arguments ===")
    println("\n=== Building Plugin Tree (With Custom Args) ===")

    libraryTree {
        rootPlugin(
            plugin = StartPlugin(),
            input = "initial-config = ${this@initZapLib.environment}"
        ) { startPluginResult ->
            println("StartPlugin\t\t\tcompleted with: $startPluginResult")

            plugin(
                plugin = AuthPlugin(),
                arg = startPluginResult
            ) { authPluginResult ->
                println("AuthPlugin\t\t\tcompleted with: $authPluginResult")

                plugin(
                    plugin = SyncPlugin(),
                    arg = "$startPluginResult|$authPluginResult"
                ) { syncPluginResult ->
                    println("SyncPlugin\t\t\tcompleted with: $syncPluginResult")
                    println("Combined input from both parents!")
                }

                plugin(
                    plugin = ValidationPlugin(),
                    arg = authPluginResult
                ) { validationPluginResult ->
                    println("ValidationPlugin\t\tcompleted with: $validationPluginResult")
                }
            }

            plugin(
                plugin = DatabasePlugin(),
                arg = startPluginResult
            ) { databasePluginResult: String ->
                println("DatabasePlugin\t\t\tcompleted with: $databasePluginResult")

                plugin(plugin = NotificationPlugin())
            }
        }
    }

    println("\nPlugin tree built successfully with custom arguments!")
}