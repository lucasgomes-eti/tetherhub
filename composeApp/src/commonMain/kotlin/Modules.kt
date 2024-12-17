import auth.login.LoginScreenModel
import auth.login.network.LoginClient
import auth.registration.RegistrationScreenModel
import auth.registration.network.RegistrationClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

val authModule = module {
    singleOf(::RegistrationClient)
    singleOf(::LoginClient)
    factory { RegistrationScreenModel(get()) }
    factory { LoginScreenModel(get()) }
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(platformModule, authModule)
    }
}