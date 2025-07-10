import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import network.BaseUrl
import network.HttpClientManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val platformModule = module {
    single {
        HttpClientManager(
            engine = OkHttp.create(),
            preferences = get(),
            baseUrl = get(),
            eventBus = get()
        )
    }
    single<BaseUrl> {
        BaseUrl(
            protocol = "https://",
            host = "tetherhub-production.up.railway.app",
            port = SERVER_PORT
        )
    }
    single {
        createDataStore(androidApplication())
    }
    single {
        PermissionsController(androidApplication())
    }
    single {
        getDatabaseBuilder(androidApplication())
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}