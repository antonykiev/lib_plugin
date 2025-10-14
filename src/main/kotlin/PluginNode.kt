package org.pet.project

class PluginNode<T, R>(
    val plugin: Plugin<T, R>,
    val executor: (T?) -> R,
    val children: MutableList<PluginNode<*, *>> = mutableListOf()
)