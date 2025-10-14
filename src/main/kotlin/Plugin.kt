package org.pet.project


interface Plugin<INPUT, OUTPUT> {
    fun execute(input: INPUT? = null): OUTPUT
}