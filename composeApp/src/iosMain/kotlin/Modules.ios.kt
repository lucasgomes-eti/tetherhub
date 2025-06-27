import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import network.BaseUrl
import network.HttpClientManager
import org.koin.dsl.module
import dev.icerock.moko.permissions.ios.PermissionsController as iOSPermissionsController

actual val platformModule = module {
    single {
        HttpClientManager(
            engine = Darwin.create(),
            preferences = get(),
            baseUrl = BaseUrl(
                protocol = "http://",
                host = "localhost",
                port = SERVER_PORT
            ),
            eventBus = get()
        )
    }
    single { createDataStore() }
    single<PermissionsController> { iOSPermissionsController() }
    single {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}