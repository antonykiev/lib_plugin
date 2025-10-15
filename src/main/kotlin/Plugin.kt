

interface Plugin<T, R> {
    suspend fun execute(input: T? = null): R
}