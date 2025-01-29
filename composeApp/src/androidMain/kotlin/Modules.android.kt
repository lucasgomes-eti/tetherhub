import io.ktor.client.engine.okhttp.OkHttp
import network.HttpClientManager
import org.koin.dsl.module

actual val platformModule = module {
    single { HttpClientManager(engine = OkHttp.create(), baseUrl = "http://10.0.2.2:8082") }
}