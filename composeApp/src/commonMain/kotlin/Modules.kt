import auth.login.LoginScreenModel
import auth.login.network.LoginClient
import auth.registration.RegistrationScreenModel
import auth.registration.network.RegistrationClient
import feed.CreatePostScreenModel
import feed.FeedClient
import feed.FeedScreenModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import profile.ProfileClient
import profile.ProfileScreenModel

expect val platformModule: Module

val authModule = module {
    singleOf(::RegistrationClient)
    singleOf(::LoginClient)
    factory { RegistrationScreenModel(get()) }
    factory { LoginScreenModel(get()) }
}

val profileModule = module {
    singleOf(::ProfileClient)
    factory { ProfileScreenModel(get()) }
}

val feedModule = module {
    singleOf(::FeedClient)
    factory { FeedScreenModel(get()) }
    factory { CreatePostScreenModel(get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, authModule, profileModule, feedModule)
    }
}