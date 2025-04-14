package kg.automoika.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import kg.automoika.db.AuthDatabase
import kg.automoika.db.CarWashDatabase
import kg.automoika.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single<CarWashRepository> { CarWashRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
    single<CommonRepository> { CommonRepositoryImpl(get(), get()) }
    single<StoriesRepository> { StoriesRepositoryImpl(get()) }
}

val dataBaseModule = module {
    single { AppData() }
    single { CarWashDatabase }
    single { AuthDatabase }
}

fun mongoModule(uri : String, db : String) = module {
    single { MongoClient.create(uri)}
    single { get<MongoClient>().getDatabase(db) }
}

