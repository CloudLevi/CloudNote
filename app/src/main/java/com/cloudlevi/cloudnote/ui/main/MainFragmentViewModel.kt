package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.data.NoteDao
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MainFragmentViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao
):ViewModel() {

    private val foldersFlow = noteDao.getAllFolders()
    private val notesFlow = noteDao.getAllNotes()

    var folders = foldersFlow.asLiveData()

    var notes = notesFlow.asLiveData()

    var searchQuery = MutableLiveData<String>()

    var queryNotesFlow = searchQuery.asFlow().flatMapLatest {
        noteDao.getNotesByQuery(it)
    }

    var queryNotesLiveData = queryNotesFlow.asLiveData()

    var dataList = concatenateLists(folders.value, notes.value)
    set(value) {
        field = value
        Log.d(TAG, "data list setter called")
        folderNoteLiveData.value = value
    }
    val folderNoteLiveData = MutableLiveData<List<Any?>>()

    fun notesChanged(noteList: List<Note>){
        if(folders.value != null) dataList = concatenateLists(folders.value, noteList)
    }

    fun foldersChanged(folderList: List<Folder>){
        if (notes.value != null) dataList = concatenateLists(folderList, notes.value)
    }

    fun onDeleteItem(item: Any){
        when(item){
            is Note -> deleteNote(item)
            is Folder -> deleteFolder(item)
        }
    }

    fun queryResultChanged(list: List<Note>){
        if (searchQuery.value == "") dataList = concatenateLists(folders.value, notes.value)
        else dataList = list
    }

    private fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }

    private fun deleteFolder(folder: Folder){
        viewModelScope.launch {
            noteDao.deleteFolderContents(folder.id)
            noteDao.deleteFolder(folder.id)
        }
    }

    private fun concatenateLists(folderList: List<Folder>?, notesList: List<Note>?): List<Any?>{
        val list: MutableList<Any> = ArrayList()
        if (folderList != null && notesList != null){
            list.addAll(folderList)
            list.addAll(notesList)
        }
        return list
    }

}