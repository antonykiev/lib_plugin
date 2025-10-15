

suspend fun libraryTree(block: suspend PluginTreeBuilder.() -> Unit) {
    PluginTreeBuilder().apply {
        block()
    }
}



class PluginTreeBuilder {
    private val allPlugins = mutableMapOf<Class<*>, PluginNode<*, *>>()
    private val executionContext = mutableMapOf<Class<*>, Any>()

    suspend fun <T : Plugin<I, O>, I, O> rootPlugin(
        plugin: T,
        input: I? = null,
        block: suspend PluginBranchBuilder<T, I, O>.(O) -> Unit
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