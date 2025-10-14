package org.pet.project

class PluginBranchBuilder<ParentT : Plugin<PI, PO>, PI, PO>(
    val parentNode: PluginNode<PI, PO>
) {
    inline fun <reified T : Plugin<I, O>, I, O> plugin(
        plugin: T,
        noinline executor: (I?) -> O,
        block: PluginBranchBuilder<T, I, O>.() -> Unit = {}
    ) {
        val node = PluginNode(plugin, executor)
        parentNode.children.add(node)

        PluginBranchBuilder<T, I, O>(node).block()
    }
}