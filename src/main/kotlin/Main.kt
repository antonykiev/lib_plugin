package org.pet.project

interface Plugin {
    fun execute()
}

class ServiceLocator private constructor() : Plugin {
    @PublishedApi
    internal val services = mutableMapOf<Class<*>, Plugin>()

    @PublishedApi
    internal val factories = mutableMapOf<Class<*>, ServiceLocator.() -> Plugin>()

    class Builder {
        @PublishedApi
        internal val factories = mutableMapOf<Class<*>, ServiceLocator.() -> Plugin>()

        inline fun <reified T : Plugin> registerLazy(noinline factory: ServiceLocator.() -> T): Builder {
            this.factories[T::class.java] = factory as ServiceLocator.() -> Plugin
            return this
        }

        fun build(): ServiceLocator {
            val locator = ServiceLocator()
            locator.factories.putAll(factories)
            return locator
        }
    }

    inline fun <reified T : Plugin> get(): T {
        // Check if already instantiated
        services[T::class.java]?.let {
            return it as T
        }

        // Check factories and instantiate
        factories[T::class.java]?.let { factory ->
            val service = factory(this)
            services[T::class.java] = service
            factories.remove(T::class.java)
            return service as T
        }

        throw IllegalStateException("Service ${T::class.java.simpleName} not found")
    }

    inline fun <reified T : Plugin> isRegistered(): Boolean {
        return services.containsKey(T::class.java) || factories.containsKey(T::class.java)
    }

    fun clear() {
        services.clear()
        factories.clear()
    }

    override fun execute() {
        factories.keys.toList().forEach { key ->
            // Force instantiation of all services
            val service = factories[key]?.invoke(this)
            if (service != null) {
                services[key] = service
            }
        }
        factories.clear()

        // Execute all instantiated services
        services.values.forEach { it.execute() }
    }
}

class AuthenticationService(
    private val username: String,
    private val password: String
) : Plugin {
    override fun execute() {
        println("AuthenticationService execute...")
    }
}

class UserService(
    private val username: String,
    private val authenticationService: AuthenticationService
) : Plugin {
    override fun execute() {
        println("start UserService=======")
        println("UserService execute...")
        authenticationService.execute()
        println("finish UserService======")
    }
}

fun main() {
    val lib = ServiceLocator.Builder()

        .registerLazy {
            UserService(
                username = "admin",
                authenticationService = get<AuthenticationService>()
            )
        }
        .registerLazy {
            AuthenticationService(
                username = "admin",
                password = "1234"
            )
        }
        .build()
        .execute()
}