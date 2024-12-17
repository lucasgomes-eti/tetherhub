import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

actual val platformModule = module {
    single { createHttpClient(engine = OkHttp.create(), baseUrl = "http://10.0.2.2:8082") }
}