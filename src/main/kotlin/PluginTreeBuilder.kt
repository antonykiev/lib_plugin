package org.pet.project

fun libraryTree(block: PluginTreeBuilder.() -> Unit): PluginRegistry {
    return PluginTreeBuilder().apply(block).build()
}

class PluginTreeBuilder {
    val registry = PluginRegistry()

    inline fun <reified T : Plugin<I, O>, I, O> rootPlugin(
        plugin: T,
        noinline executor: (I?) -> O,
        block: PluginBranchBuilder<T, I, O>.() -> Unit = {}
    ): PluginTreeBuilder {
        val node = PluginNode(plugin, executor)
        registry.allPlugins[T::class.java] = node
        registry.roots.add(node)

        val branchBuilder = PluginBranchBuilder<T, I, O>(node)
        branchBuilder.block()

        return this
    }

    fun build(): PluginRegistry = registry
}

