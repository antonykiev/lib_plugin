package org.pet.project

class PluginRegistry {
    val roots = mutableListOf<PluginNode<*, *>>()
    val allPlugins = mutableMapOf<Class<*>, PluginNode<*, *>>()

    private fun <T, R> getNode(pluginClass: Class<out Plugin<T, R>>): PluginNode<T, R>? {
        return allPlugins[pluginClass] as? PluginNode<T, R>
    }

    fun <T, R> execute(pluginClass: Class<out Plugin<T, R>>, input: T? = null): R {
        val node = getNode(pluginClass)
            ?: throw IllegalArgumentException("Plugin ${pluginClass.simpleName} not found")
        return node.executor(input)
    }

    fun <T, R> executeTree(pluginClass: Class<out Plugin<T, R>>, input: T? = null): Map<Class<*>, Any> {
        val results = mutableMapOf<Class<*>, Any>()

        fun executeNode(node: PluginNode<*, *>, parentInput: Any?, parentResult: Any?): Any {
            val inputForNode = parentResult ?: parentInput

            @Suppress("UNCHECKED_CAST")
            val currentNode = node as PluginNode<Any?, Any>
            val result = currentNode.executor(inputForNode)
            results[node.plugin::class.java] = result

            node.children.forEach { child ->
                executeNode(
                    node = child,
                    parentInput = inputForNode,
                    parentResult = result
                )
            }

            return result
        }

        val rootNode = getNode(pluginClass)
            ?: throw IllegalArgumentException("Plugin ${pluginClass.simpleName} not found")
        executeNode(
            node = rootNode,
            parentInput = input,
            parentResult = null
        )

        return results
    }
}