package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.data.NoteDao

class MainFragmentViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao
):ViewModel() {

    private val foldersFlow = noteDao.getAllFolders()
    private val notesFlow = noteDao.getAllNotes()

    var folders = foldersFlow.asLiveData()

    var notes = notesFlow.asLiveData()

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

    fun onItemSwiped(item: Any){
        //TODO
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