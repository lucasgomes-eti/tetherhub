import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformModule = module {
    single { HttpClientManager(engine = Darwin.create(), baseUrl = "http://localhost:8082") }
}