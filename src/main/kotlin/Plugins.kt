package org.pet.project

class StartPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        val data = input ?: "default-data"
        println("🌱 StartPlugin executing with: $data")
        return "start-result-$data"
    }
}

class AuthPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("🔐 AuthPlugin executing with: $input")
        return "auth-result-$input-${System.currentTimeMillis()}"
    }
}

class SyncPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("🔄 SyncPlugin executing with: $input")
        return "sync-complete-$input"
    }
}

class ValidationPlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("✅ ValidationPlugin executing with: $input")
        return "validated-$input"
    }
}

class DatabasePlugin : Plugin<String, String> {
    override fun execute(input: String?): String {
        println("🗄️ DatabasePlugin executing with: $input")
        return "db-ready-$input"
    }
}