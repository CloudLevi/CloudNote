package com.cloudlevi.cloudnote.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes_table ORDER BY date_modified DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE folder = -1 ORDER BY pinned DESC, date_modified DESC")
    fun getAllNotesWithoutFolder(): Flow<List<Note>>

    @Query("SELECT * FROM folders_table ORDER BY date_modified DESC")
    fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders_table WHERE id = :folderID")
    fun getFolderByID(folderID: Int): Flow<Folder>

    @Query("SELECT * FROM notes_table WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') ORDER BY pinned DESC, date_modified DESC")
    fun getNotesByQuery(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND folder = :folderID ORDER BY pinned DESC, date_modified DESC")
    fun getFolderNotesByQuery(query: String, folderID: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes_table WHERE folder = :folderID ORDER BY pinned DESC, date_modified DESC")
    fun getFolderContents(folderID: Int): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: Folder)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM folders_table WHERE id = :folderID")
    suspend fun deleteFolder(folderID: Int)

    @Query("DELETE FROM notes_table WHERE folder = :folderID")
    suspend fun deleteFolderContents(folderID: Int)
}