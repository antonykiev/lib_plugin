package org.pet.project

interface Plugin<T, R> {
    fun execute(input: T? = null): R
}

class PluginNode<T, R>(
    val pluginClass: Class<out Plugin<T, R>>,
    val executor: (T?) -> R,
    val children: MutableList<PluginNode<*, *>> = mutableListOf()
)

class PluginRegistry {
    val roots = mutableListOf<PluginNode<*, *>>()
    val allPlugins = mutableMapOf<Class<*>, PluginNode<*, *>>()

    fun <T, R> getNode(pluginClass: Class<out Plugin<T, R>>): PluginNode<T, R>? {
        return allPlugins[pluginClass] as? PluginNode<T, R>
    }

    fun <T, R> execute(pluginClass: Class<out Plugin<T, R>>, input: T? = null): R {
        val node = getNode(pluginClass) ?: throw IllegalArgumentException("Plugin ${pluginClass.simpleName} not found")
        return node.executor(input)
    }

    fun <T, R> executeTree(pluginClass: Class<out Plugin<T, R>>, input: T? = null): Map<Class<*>, Any> {
        val results = mutableMapOf<Class<*>, Any>()

        fun executeNode(node: PluginNode<*, *>, parentInput: Any?, parentResult: Any?): Any {
            // Use parent result as input if available, otherwise use provided input
            val inputForNode = parentResult ?: parentInput

            @Suppress("UNCHECKED_CAST")
            val currentNode = node as PluginNode<Any?, Any>
            val result = currentNode.executor(inputForNode)
            results[node.pluginClass] = result

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
    val registry = PluginRegistry()

    inline fun <reified T : Plugin<I, O>, I, O> rootPlugin(
        noinline executor: (I?) -> O,
        block: PluginBranchBuilder<T, I, O>.() -> Unit = {}
    ): PluginTreeBuilder {
        val node = PluginNode(T::class.java, executor)
        registry.allPlugins[T::class.java] = node
        registry.roots.add(node)

        // Create branch builder and apply the block
        val branchBuilder = PluginBranchBuilder<T, I, O>(node)
        branchBuilder.block()

        return this
    }

    fun build(): PluginRegistry = registry
}

class PluginBranchBuilder<ParentT : Plugin<PI, PO>, PI, PO>(
    val parentNode: PluginNode<PI, PO>
) {
    inline fun <reified T : Plugin<I, O>, I, O> plugin(
        noinline executor: (I?) -> O,
        block: PluginBranchBuilder<T, I, O>.() -> Unit = {}
    ) {
        val node = PluginNode(T::class.java, executor)
        parentNode.children.add(node)

        PluginBranchBuilder<T, I, O>(node).block()
    }
}

fun pluginTree(block: PluginTreeBuilder.() -> Unit): PluginRegistry {
    return PluginTreeBuilder().apply(block).build()
}

interface StartPlugin : Plugin<String, String>
interface AuthPlugin : Plugin<String, String>
interface SyncPlugin : Plugin<String, String>
interface ValidationPlugin : Plugin<String, String>
interface DatabasePlugin : Plugin<String, String>

fun main() {
    println("=== Fixed Tree-Based Plugin System ===")

    val registry = pluginTree {
        // Make sure the executor lambda is properly placed
        rootPlugin<StartPlugin, String, String>(
            executor = { input ->
                val data = input ?: "default-data"
                println("🌱 StartPlugin executing with: $data")
                "start-result-$data"
            }
        ) {
            plugin<AuthPlugin, String, String>(
                executor = { parentResult ->
                    println("🔐 AuthPlugin received from parent: $parentResult")
                    "auth-result-$parentResult"
                }
            ) {
                plugin<SyncPlugin, String, String>(
                    executor = { authResult ->
                        println("🔄 SyncPlugin received from auth: $authResult")
                        "sync-complete-$authResult"
                    }
                )

                plugin<ValidationPlugin, String, String>(
                    executor = { authResult ->
                        println("✅ ValidationPlugin received from auth: $authResult")
                        "validated-$authResult"
                    }
                )
            }

            plugin<DatabasePlugin, String, String>(
                executor = { startResult ->
                    println("🗄️ DatabasePlugin received from start: $startResult")
                    "db-ready-$startResult"
                }
            )
        }
    }

    println("\n=== Executing Plugin Tree ===")
    val results = registry.executeTree(StartPlugin::class.java, "test-input")

    println("\n=== All Results ===")
    results.forEach { (pluginClass, result) ->
        println("${pluginClass.simpleName}: $result")
    }
}