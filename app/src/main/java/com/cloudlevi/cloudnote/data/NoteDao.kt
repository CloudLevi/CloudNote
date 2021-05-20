package com.cloudlevi.cloudnote.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes_table ORDER BY date_modified DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM folders_table ORDER BY date_modified DESC")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM notes_table WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY date_modified DESC")
    fun getNotesByQuery(query: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM folders_table WHERE id = :folderID")
    suspend fun deleteFolder(folderID: Int)

    @Query("DELETE FROM notes_table WHERE folder = :folderID")
    suspend fun deleteFolderContents(folderID: Int)
}