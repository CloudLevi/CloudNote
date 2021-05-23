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
                val folder1 = Folder("Folder 1)")
                val folder2 = Folder("Folder 2)")

                val note1 = Note("Wash the dishes")
                note1.apply {
                    description = "lorem ipsum lalalalalallalalalala"
                    background_color = "#FF018786"
                    pinned = true
                    folder = 1
                }

                val note2 = Note("How to create an engine")
                note2.apply {
                    description = "lorem ipsum lalalalalallalalalala"
                    background_color = "#F3018726"
                    password = "0000"
                    folder = 1
                }

                val note3 = Note("Listen to Kanye")
                note3.apply {
                    description = "lorem ipsum lalalalalallalalalala"
                    background_color = "#F3518266"
                    folder = 1
                }

                val note4 = Note("Go shopping")
                note4.apply {
                    description = "lorem ipsum lalalalalallalalalala"
                    background_color = "#F3516216"
                    folder = 2
                }

                val note5 = Note("Passwords")
                note5.apply {
                    description = "lorem ipsum lalalalalallalalalala"
                    background_color = "#F3516216"
                    password = "0000"
                    folder = 2
                }

                val note6 = Note("List of satisfied customers")
                note6.apply {
                    description = "lorem ipsum lalalalalallalalalala"
                    background_color = "#F3516216"
                    pinned = true
                    folder = 2
                }

                val note7 = Note("List of satisfied customers")
                note7.apply {
                    description = "Mauris euismod commodo gravida. Integer ac lobortis lectus. Phasellus eget pretium arcu. Proin lobortis tempus diam. Donec efficitur felis ut ornare convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec molestie nibh vel libero blandit, ut accumsan urna maximus. Donec quis ullamcorper sem. Duis ut fringilla massa, sed ultrices ex. Curabitur sed nibh odio. Quisque gravida libero at orci blandit gravida."
                    background_color = "#F3516216"
                }

                val note8 = Note("How to harvest beets")
                note8.apply {
                    description = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."
                    background_color = "#e042f5"
                    password = "0000"
                }

                val note9 = Note("Answers for monday's test")
                note9.apply {
                    description = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like)."
                    background_color = "#42f590"
                    pinned = true
                    hideTitle = true
                    password = "0000"
                }

                val note10 = Note("Albums of Cudi")
                note10.apply {
                    description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
                    background_color = "#42f590"
                    pinned = true
                }

                dao.insertNote(note1)
                dao.insertNote(note2)
                dao.insertNote(note3)
                dao.insertNote(note4)
                dao.insertNote(note5)
                dao.insertNote(note6)
                dao.insertNote(note7)
                dao.insertNote(note8)
                dao.insertNote(note9)
                dao.insertNote(note10)

                dao.insertFolder(folder1)
                dao.insertFolder(folder2)
            }

        }
    }
}