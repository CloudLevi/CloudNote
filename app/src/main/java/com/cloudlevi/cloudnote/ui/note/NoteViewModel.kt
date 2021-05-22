package com.cloudlevi.cloudnote.ui.note

import android.content.ContentValues.TAG
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.data.NoteDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import com.cloudlevi.cloudnote.ui.note.NoteEvent.*
import kotlinx.coroutines.flow.receiveAsFlow

class NoteViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao
): ViewModel() {

    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    val foldersLiveData = noteDao.getAllFolders().asLiveData()

    var folder = Folder("")
    var currentNote = Note("")

    fun onDeleteClicked() = viewModelScope.launch {
        noteDao.deleteNote(currentNote)
        if (currentNote.folder != -1) navigateToFolder(folder)
        else navigateToMainFragment()
    }

    fun onFoldersLiveDataChanged(foldersList: List<Folder>){
        for (currentFolder in foldersList){
            if (currentFolder.id == currentNote.folder) {
                folder = currentFolder
            }
        }
    }

    fun onPinClicked(){
        when(currentNote.pinned){
            true -> {
                currentNote.pinned = false
                currentNote.date_modified = System.currentTimeMillis()
                sendToastMessage("Note unpinned!")
                updateNote(currentNote)
            }
            false -> {
                currentNote.pinned = true
                currentNote.date_modified = System.currentTimeMillis()
                sendToastMessage("Note pinned!")
                updateNote(currentNote)
            }
        }
    }

    fun updateBGColor(colorHex: String){
        currentNote.background_color = colorHex
        currentNote.date_modified = System.currentTimeMillis()
        updateNote(currentNote)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
    }

    private fun sendToastMessage(message: String) = viewModelScope.launch {
        noteEventChannel.send(SendToastMessage(message))
    }

    private fun navigateToFolder(folder: Folder) = viewModelScope.launch {
        noteEventChannel.send(NavigateToFolder(folder))
    }

    private fun navigateToMainFragment() = viewModelScope.launch {
        noteEventChannel.send(NavigateToMainFragment)
    }
}

sealed class NoteEvent{
    data class NavigateToFolder(val folder: Folder): NoteEvent()
    data class SendToastMessage(val message: String): NoteEvent()
    object  NavigateToMainFragment: NoteEvent()
}