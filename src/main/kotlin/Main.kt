

interface Plugin<T, R> {
    fun execute(input: T? = null): R
}

class PluginNode<T, R>(
    val plugin: Plugin<T, R>,
    val children: MutableList<PluginNode<*, *>> = mutableListOf()
) {
    var cachedResult: R? = null
    var hasExecuted: Boolean = false

    fun execute(input: T? = null): R {
        if (!hasExecuted) {
            cachedResult = plugin.execute(input)
            hasExecuted = true
        }
        return cachedResult!!
    }

    fun reset() {
        hasExecuted = false
        cachedResult = null
        children.forEach { it.reset() }
    }
}

class PluginRegistry {
    val roots = mutableListOf<PluginNode<*, *>>()
    val allPlugins = mutableMapOf<Class<*>, PluginNode<*, *>>()

    private fun <T, R> getNode(pluginClass: Class<out Plugin<T, R>>): PluginNode<T, R>? {
        return allPlugins[pluginClass] as? PluginNode<T, R>
    }

    fun <T, R> execute(pluginClass: Class<out Plugin<T, R>>, input: T? = null): R {
        val node = getNode(pluginClass) ?: throw IllegalArgumentException("Plugin ${pluginClass.simpleName} not found")
        return node.execute(input)
    }

    fun <T, R> executeTree(pluginClass: Class<out Plugin<T, R>>, input: T? = null): Map<Class<*>, Any> {
        val results = mutableMapOf<Class<*>, Any>()

        // Reset all plugins before execution
        roots.forEach { it.reset() }

        fun executeNode(node: PluginNode<*, *>, parentInput: Any?, parentResult: Any?): Any {
            val inputForNode = parentResult ?: parentInput

            @Suppress("UNCHECKED_CAST")
            val currentNode = node as PluginNode<Any?, Any>
            val result = currentNode.execute(inputForNode)
            results[node.plugin::class.java] = result

            node.children.forEach { child ->
                executeNode(child, inputForNode, result)
            }

            return result
        }

        val rootNode = getNode(pluginClass) ?: throw IllegalArgumentException("Plugin ${pluginClass.simpleName} not found")
        executeNode(rootNode, input, null)

        return results
    }
}

class PluginTreeBuilder {
    private val registry = PluginRegistry()
    private val executionContext = mutableMapOf<Class<*>, Any>()

    fun <T : Plugin<I, O>, I, O> rootPlugin(
        plugin: T,
        input: I? = null,
        block: PluginBranchBuilder<T, I, O>.(O) -> Unit
    ): PluginTreeBuilder {
        val node = PluginNode(plugin)
        registry.allPlugins[plugin::class.java] = node
        registry.roots.add(node)

        val result = plugin.execute(input)
        executionContext[plugin::class.java] = result as Any

        val branchBuilder = PluginBranchBuilder<T, I, O>(node, executionContext)
        branchBuilder.block(result)

        return this
    }

    fun build(): PluginRegistry = registry
}

class PluginBranchBuilder<ParentT : Plugin<PI, PO>, PI, PO>(
    private val parentNode: PluginNode<PI, PO>,
    private val executionContext: MutableMap<Class<*>, Any>
) {
    fun <T : Plugin<I, O>, I, O> plugin(
        plugin: T,
        arg: I? = null,
        block: PluginBranchBuilder<T, I, O>.(O) -> Unit
    ) {
        val node = PluginNode(plugin)
        parentNode.children.add(node)

        val input = arg ?: (executionContext[parentNode.plugin::class.java] as? PI)
        val result = plugin.execute(input as? I)
        executionContext[plugin::class.java] = result as Any

        val branchBuilder = PluginBranchBuilder<T, I, O>(node, executionContext)
        branchBuilder.block(result)
    }

    fun <T : Plugin<I, O>, I, O> plugin(
        plugin: T,
        arg: I? = null
    ) {
        val node = PluginNode(plugin)
        parentNode.children.add(node)

        val input = arg ?: (executionContext[parentNode.plugin::class.java] as? PI)
        val result = plugin.execute(input as? I)
        executionContext[plugin::class.java] = result as Any
    }
}

fun libraryTree(block: PluginTreeBuilder.() -> Unit): PluginRegistry {
    return PluginTreeBuilder().apply(block).build()
}

class StartPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        val data = input ?: "default-data"
        println("🌱 StartPlugin executing with: $data")
        return "start-result-$data"
    }
}

class AuthPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("🔐 AuthPlugin executing with: $input")
        return "auth-result-$input-${System.currentTimeMillis()}"
    }
}

class SyncPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("🔄 SyncPlugin executing with: $input")
        return "sync-complete-$input"
    }
}

class ValidationPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("✅ ValidationPlugin executing with: $input")
        return "validated-$input"
    }
}

class DatabasePlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("🗄️ DatabasePlugin executing with: $input")
        return "db-ready-$input"
    }
}

class NotificationPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("📢 NotificationPlugin executing with: $input")
        return "notified-$input"
    }
}

fun main() {
    println("=== Plugin Tree System with Custom Arguments ===")

    val startPlugin = StartPlugin()
    val authPlugin = AuthPlugin()
    val syncPlugin = SyncPlugin()
    val validationPlugin = ValidationPlugin()
    val databasePlugin = DatabasePlugin()
    val notificationPlugin = NotificationPlugin()

    println("\n=== Building Plugin Tree (With Custom Args) ===")

    val registry = libraryTree {
        rootPlugin(
            plugin = startPlugin,
            input = "initial-config"
        ) { startPluginResult ->
            println("📊 StartPlugin completed with: $startPluginResult")

            plugin(
                plugin = authPlugin,
                arg = startPluginResult
            ) { authPluginResult ->
                println("📊 AuthPlugin completed with: $authPluginResult")

                plugin(
                    plugin = syncPlugin,
                    arg = "$startPluginResult|$authPluginResult"
                ) { syncPluginResult ->
                    println("📊 SyncPlugin completed with: $syncPluginResult")
                    println("   ↳ Combined input from both parents!")
                }

                plugin(
                    plugin = validationPlugin,
                    arg = authPluginResult
                ) { validationPluginResult ->
                    println("📊 ValidationPlugin completed with: $validationPluginResult")
                }
            }

            plugin(
                plugin = databasePlugin,
                arg = startPluginResult
            ) { databasePluginResult: String ->
                println("📊 DatabasePlugin completed with: $databasePluginResult")

                plugin(
                    plugin = notificationPlugin,
                    arg = databasePluginResult
                ) { notificationPluginResult ->
                    println("📊 NotificationPlugin completed with: $notificationPluginResult")
                }
            }
        }
    }

    println("\n✅ Plugin tree built successfully with custom arguments!")

//    println("\n=== Advanced Examples with Custom Args ===")
//
//    val advancedRegistry = libraryTree {
//        rootPlugin(plugin = startPlugin, input = "advanced-setup") { startResult ->
//            println("\n🔧 Building complex pipeline...")
//
//            plugin(
//                plugin = authPlugin,
//                arg = "custom-auth-input-based-on-$startResult"
//            ) { authResult ->
//                println("📊 Custom auth with special input: $authResult")
//            }
//
//            plugin(
//                plugin = syncPlugin,
//                arg = startResult.uppercase()
//            ) { syncResult ->
//                println("📊 Sync with uppercase input: $syncResult")
//            }
//
//            plugin(plugin = authPlugin, arg = startResult) { authResult2 ->
//                val combinedArg = "combined:${startResult.take(5)}|${authResult2.take(5)}"
//                plugin(
//                    plugin = validationPlugin,
//                    arg = combinedArg
//                ) { validationResult ->
//                    println("📊 Validation with combined args: $validationResult")
//                }
//            }
//        }
//    }
//
//    println("\n✅ Advanced examples completed!")
}