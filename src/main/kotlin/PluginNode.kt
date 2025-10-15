

class PluginNode<T, R>(
    val plugin: Plugin<T, R>,
    val children: MutableList<PluginNode<*, *>> = mutableListOf()
)