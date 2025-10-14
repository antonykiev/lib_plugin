package org.pet.project

fun main() {
    println("=== Concrete Plugin Classes System ===")

    val startPlugin = StartPlugin()
    val authPlugin = AuthPlugin()
    val syncPlugin = SyncPlugin()
    val validationPlugin = ValidationPlugin()
    val databasePlugin = DatabasePlugin()

    val registry = libraryTree {
        rootPlugin(
            plugin = startPlugin,
            executor = { input -> startPlugin.execute(input) }
        ) {
            plugin(
                plugin = authPlugin,
                executor = { parentResult -> authPlugin.execute(parentResult) }
            ) {
                plugin(
                    plugin = syncPlugin,
                    executor = { authResult -> syncPlugin.execute(authResult) }
                )

                plugin(
                    plugin = validationPlugin,
                    executor = { authResult -> validationPlugin.execute(authResult) }
                )
            }

            plugin(
                plugin = databasePlugin,
                executor = { startResult -> databasePlugin.execute(startResult) }
            )
        }
    }

    println("\n=== Executing Plugin Tree ===")
    val results = registry.executeTree(StartPlugin::class.java, "test-input")

    println("\n=== All Results ===")
    results.forEach { (pluginClass, result) ->
        println("${pluginClass.simpleName}: $result")
    }

    println("\n=== Testing Individual Plugin Execution ===")
    val individualResult = startPlugin.execute("individual-test")
    println("Individual StartPlugin result: $individualResult")
}