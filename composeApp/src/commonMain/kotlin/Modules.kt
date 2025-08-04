import accountOptions.AccountOptionsClient
import accountOptions.AccountOptionsScreenModel
import auth.login.LoginClient
import auth.login.LoginScreenModel
import auth.registration.RegistrationClient
import auth.registration.RegistrationScreenModel
import dsl.eventbus.EventBus
import friends.FriendsClient
import friends.FriendsScreenModel
import home.HomeScreenModel
import messages.ChatClient
import messages.chat.ChatScreenModel
import messages.chat.data.MessageDao
import messages.chat.data.MessageRepository
import messages.newroom.NewRoomScreenModel
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
import profile.search.SearchProfileScreenModel
import splash.SplashScreenModel

expect val platformModule: Module

val appModule = module {
    singleOf(::EventBus)
    factory { params -> SplashScreenModel(params.getOrNull(), get()) }
    factoryOf(::HomeScreenModel)
    singleOf(::PushNotificationManager)
}

val authModule = module {
    singleOf(::RegistrationClient)
    singleOf(::LoginClient)
    factory { RegistrationScreenModel(get()) }
    factoryOf(::LoginScreenModel)
}

val profileModule = module {
    singleOf(::ProfileClient)
    factoryOf(::ProfileScreenModel)
    factory { params -> SearchProfileScreenModel(params.get(), get(), get()) }
}

val postsModule = module {
    singleOf(::PostClient)
    factory { params -> FeedScreenModel(params.getOrNull(), get(), get()) }
    factory { params -> CreateOrEditPostScreenModel(get(), get(), params.getOrNull()) }
}

val messagesModule = module {
    singleOf(::ChatClient)
    factory { params -> RoomsScreenModel(params.getOrNull(), get(), get(), get()) }
    factoryOf(::ChatScreenModel)
    factoryOf(::NewRoomScreenModel)
    single<MessageDao> { get<AppDatabase>().messageDao() }
    singleOf(::MessageRepository)
}

val friendsModule = module {
    singleOf(::FriendsScreenModel)
    singleOf(::FriendsClient)
}

val accountOptionsModule = module {
    factoryOf(::AccountOptionsScreenModel)
    singleOf(::AccountOptionsClient)
}

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            platformModule,
            appModule,
            authModule,
            profileModule,
            postsModule,
            messagesModule,
            friendsModule,
            accountOptionsModule
        )
    }
}

internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"