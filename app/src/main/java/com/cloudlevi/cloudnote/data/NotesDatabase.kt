package com.cloudlevi.cloudnote.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cloudlevi.cloudnote.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Note::class, Folder::class], version = 1)
abstract class NotesDatabase : RoomDatabase(){

    abstract fun notesDao(): NoteDao

    class Callback @Inject constructor(
        private val database: Provider<NotesDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().notesDao()

            applicationScope.launch {
                dao.insertNote(Note("Wash the dishes"))
                dao.insertNote(Note("Repair the car"))

                dao.insertFolder(Folder("First folder"))
                dao.insertFolder(Folder("Second folder"))
            }

        }
    }
}