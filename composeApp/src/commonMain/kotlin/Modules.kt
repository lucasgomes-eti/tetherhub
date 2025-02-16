import auth.login.LoginClient
import auth.login.LoginScreenModel
import auth.registration.RegistrationScreenModel
import auth.registration.network.RegistrationClient
import messages.ChatClient
import messages.chat.ChatScreenModel
import messages.rooms.RoomsScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import post.PostClient
import post.detail.CreateOrEditPostScreenModel
import post.feed.FeedScreenModel
import profile.ProfileClient
import profile.ProfileScreenModel

expect val platformModule: Module

val appModule = module {
    singleOf(::EventBus)
}

val authModule = module {
    singleOf(::RegistrationClient)
    singleOf(::LoginClient)
    factory { RegistrationScreenModel(get()) }
    factory { LoginScreenModel(get()) }
}

val profileModule = module {
    singleOf(::ProfileClient)
    factory { ProfileScreenModel(get(), get(), get()) }
}

val postsModule = module {
    singleOf(::PostClient)
    factory { FeedScreenModel(get(), get()) }
    factory { params -> CreateOrEditPostScreenModel(get(), get(), params.getOrNull()) }
}

val messagesModule = module {
    singleOf(::ChatClient)
    factoryOf(::RoomsScreenModel)
    factoryOf(::ChatScreenModel)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, appModule, authModule, profileModule, postsModule, messagesModule)
    }
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"