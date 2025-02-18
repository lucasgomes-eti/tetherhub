import io.ktor.client.engine.okhttp.OkHttp
import network.BaseUrl
import network.HttpClientManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val platformModule = module {
    single {
        HttpClientManager(
            engine = OkHttp.create(),
            preferences = get(),
            baseUrl = BaseUrl(
                protocol = "http://",
                host = "10.0.2.2",
                port = 8082
            )
        )
    }
    single {
        createDataStore(androidApplication())
    }
}