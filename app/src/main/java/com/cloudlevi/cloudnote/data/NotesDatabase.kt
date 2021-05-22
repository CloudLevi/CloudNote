package com.cloudlevi.cloudnote.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cloudlevi.cloudnote.R
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
                var folder1 = Folder("Folder 1)")
                var folder2 = Folder("Folder 2)")
                var folder3 = Folder("Folder 3)")
                var folder4 = Folder("Folder 4)")

                var note1 = Note("Wash the dishes")
                note1.description = "lorem ipsum lalalalalallalalalala"
                note1.background_color = "#FF018786"
                note1.pinned = true
                note1.folder = 1
                var note2 = Note("Wash the dishes2")
                note2.description = "lorem ipsum lalalalalallalalalala"
                note2.background_color = "#F3018726"
                note2.folder = 1
                var note3 = Note("Wash the dishes3")
                note3.description = "lorem ipsum lalalalalallalalalala"
                note3.background_color = "#F3518266"
                note3.folder = 1

                var note4 = Note("Repair the car")
                note4.description = "lorem ipsum lalalalalallalalalala"
                note4.background_color = "#F3516216"
                note4.folder = 2
                var note5 = Note("Repair the car")
                note5.description = "lorem ipsum lalalalalallalalalala"
                note5.background_color = "#F3516216"
                note5.folder = 2
                var note6 = Note("Wash the dishes")
                note6.description = "lorem ipsum lalalalalallalalalala"
                note6.background_color = "#F3516216"
                note6.pinned = true
                note6.folder = 2

                dao.insertNote(note1)
                dao.insertNote(note2)
                dao.insertNote(note3)
                dao.insertNote(note4)
                dao.insertNote(note5)
                dao.insertNote(note6)

                dao.insertFolder(folder1)
                dao.insertFolder(folder2)
                dao.insertFolder(folder3)
                dao.insertFolder(folder4)
            }

        }
    }
}