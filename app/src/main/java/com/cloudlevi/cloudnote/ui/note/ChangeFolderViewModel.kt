package com.cloudlevi.cloudnote.ui.note

import android.content.ContentValues
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.data.NoteDao
import kotlinx.coroutines.launch

class ChangeFolderViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao
): ViewModel() {

    var foldersLiveData = noteDao.getAllFolders().asLiveData()
    var folderTitlesHashMap = HashMap<Int, Pair<Int, String>>()
    var chosenFolder = -1
    var initialFolder = -1
    lateinit var currentNote: Note

    fun onApplyClicked(){
        viewModelScope.launch {
            currentNote.folder = chosenFolder
            noteDao.updateNote(currentNote)
        }
    }

    fun getFolderTitles(foldersList: List<Folder>): ArrayList<String> {
        val arrayList = ArrayList<String>()
        folderTitlesHashMap.clear()
        arrayList.add("None")
        folderTitlesHashMap[0] = Pair(-1, "None")

        for ((index, folder) in foldersList.withIndex()) {
            arrayList.add(folder.title)
            folderTitlesHashMap[index + 1] = Pair(folder.id, folder.title)

            if (folder.id == currentNote.folder) initialFolder = index+1
        }
        return arrayList
    }

    fun spinnerItemSelected(position: Int) {
        chosenFolder = folderTitlesHashMap[position]?.first ?: -1
        Log.d(ContentValues.TAG, "spinnerItemSelected: ${folderTitlesHashMap[position]?.first ?: -20}")
    }
}