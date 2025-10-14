package org.pet.project


/**
 * I - input arguments for plugin
 * O - output/return value of plugin
 */
interface Plugin<I, O> {
    fun execute(input: I? = null): O
}