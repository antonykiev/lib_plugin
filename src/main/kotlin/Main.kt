

interface Plugin<T, R> {
    fun execute(input: T? = null): R
}

class PluginNode<T, R>(
    val plugin: Plugin<T, R>,
    val children: MutableList<PluginNode<*, *>> = mutableListOf()
)

class PluginTreeBuilder {
    private val allPlugins = mutableMapOf<Class<*>, PluginNode<*, *>>()
    private val executionContext = mutableMapOf<Class<*>, Any>()

    fun <T : Plugin<I, O>, I, O> rootPlugin(
        plugin: T,
        input: I? = null,
        block: PluginBranchBuilder<T, I, O>.(O) -> Unit
    ): PluginTreeBuilder {
        val node = PluginNode(plugin)
        allPlugins[plugin::class.java] = node

        val result = plugin.execute(input)
        executionContext[plugin::class.java] = result as Any

        val branchBuilder = PluginBranchBuilder<T, I, O>(node, executionContext)
        branchBuilder.block(result)

        return this
    }
}

class PluginBranchBuilder<ParentT : Plugin<PI, PO>, PI, PO>(
    private val parentNode: PluginNode<PI, PO>,
    private val executionContext: MutableMap<Class<*>, Any>
) {
    fun <T : Plugin<I, O>, I, O> plugin(
        plugin: T,
        arg: I? = null
    ) {
        plugin(
            plugin = plugin,
            arg = arg,
            block = {}
        )
    }

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
}

fun libraryTree(block: PluginTreeBuilder.() -> Unit) {
    PluginTreeBuilder().apply(block)
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
    println("\n=== Building Plugin Tree (With Custom Args) ===")

    libraryTree {
        rootPlugin(
            plugin = StartPlugin(),
            input = "initial-config"
        ) { startPluginResult ->
            println("📊 StartPlugin completed with: $startPluginResult")

            plugin(
                plugin = AuthPlugin(),
                arg = startPluginResult
            ) { authPluginResult ->
                println("📊 AuthPlugin completed with: $authPluginResult")

                plugin(
                    plugin = SyncPlugin(),
                    arg = "$startPluginResult|$authPluginResult"
                ) { syncPluginResult ->
                    println("📊 SyncPlugin completed with: $syncPluginResult")
                    println("Combined input from both parents!")
                }

                plugin(
                    plugin = ValidationPlugin(),
                    arg = authPluginResult
                ) { validationPluginResult ->
                    println("📊 ValidationPlugin completed with: $validationPluginResult")
                }
            }

            plugin(
                plugin = DatabasePlugin(),
                arg = startPluginResult
            ) { databasePluginResult: String ->
                println("📊 DatabasePlugin completed with: $databasePluginResult")

                plugin(plugin = NotificationPlugin())
            }
        }
    }

    println("\n✅ Plugin tree built successfully with custom arguments!")
}