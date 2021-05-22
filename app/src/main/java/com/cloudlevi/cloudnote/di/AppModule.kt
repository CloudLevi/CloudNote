package com.cloudlevi.cloudnote.di

import android.app.Application
import androidx.room.Room
import com.cloudlevi.cloudnote.data.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: NotesDatabase.Callback

    ) = Room.databaseBuilder(app, NotesDatabase::class.java, "notes_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .openHelperFactory(SupportFactory(SQLiteDatabase.getBytes("cLoUdLeVi".toCharArray())))
            .build()



    @Provides
    fun provideTaskDao(db: NotesDatabase) = db.notesDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope