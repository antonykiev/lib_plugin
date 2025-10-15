

class StartPlugin : Plugin<String, String> {
    override suspend fun execute(input: String?): String {
        val data = input ?: "default-data"
        println("StartPlugin\t\t\texecuting with: $data")
        return "start-result-$data"
    }
}

class AuthPlugin : Plugin<String, String> {
    override suspend fun execute(input: String?): String {
        println("AuthPlugin\t\t\texecuting with: $input")
        return "auth-result-$input-${System.currentTimeMillis()}"
    }
}

class SyncPlugin : Plugin<String, String> {
    override suspend fun execute(input: String?): String {
        println("SyncPlugin\t\t\texecuting with: $input")
        return "sync-complete-$input"
    }
}

class ValidationPlugin : Plugin<String, String> {
    override suspend fun execute(input: String?): String {
        println("ValidationPlugin\t\texecuting with: $input")
        return "validated-$input"
    }
}

class DatabasePlugin : Plugin<String, String> {
    override suspend fun execute(input: String?): String {
        println("DatabasePlugin\t\t\texecuting with: $input")
        return "db-ready-$input"
    }
}

class NotificationPlugin : Plugin<String, String> {
    override suspend fun execute(input: String?): String {
        println("NotificationPlugin\t\texecuting with: $input")
        return "notified-$input"
    }
}