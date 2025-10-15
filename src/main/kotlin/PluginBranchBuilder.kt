

class PluginBranchBuilder<ParentT : Plugin<PI, PO>, PI, PO>(
    private val parentNode: PluginNode<PI, PO>,
    private val executionContext: MutableMap<Class<*>, Any>
) {
    suspend fun <T : Plugin<I, O>, I, O> plugin(
        plugin: T,
        arg: I? = null
    ) {
        plugin(
            plugin = plugin,
            arg = arg,
            block = {}
        )
    }

    suspend fun <T : Plugin<I, O>, I, O> plugin(
        plugin: T,
        arg: I? = null,
        block: suspend PluginBranchBuilder<T, I, O>.(O) -> Unit
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