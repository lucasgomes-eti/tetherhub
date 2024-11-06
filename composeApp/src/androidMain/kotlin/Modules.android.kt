import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

actual val platformModule = module {
    single { createHttpClient(OkHttp.create()) }
}