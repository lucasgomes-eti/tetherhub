import io.ktor.client.engine.darwin.Darwin
import network.BaseUrl
import network.HttpClientManager
import org.koin.dsl.module

actual val platformModule = module {
    single {
        HttpClientManager(
            engine = Darwin.create(),
            preferences = get(),
            baseUrl = BaseUrl(
                protocol = "http://",
                host = "localhost",
                port = 8082
            )
        )
    }
    single { createDataStore() }
}