package com.cloudlevi.cloudnote.ui.folderView

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.cloudlevi.cloudnote.HOME_TYPE_GRIDVIEW
import com.cloudlevi.cloudnote.HOME_TYPE_LISTVIEW
import com.cloudlevi.cloudnote.data.DatastoreManager
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.data.NoteDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.cloudlevi.cloudnote.ui.folderView.FolderViewEvent.*

class FolderViewViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao,
    private val datastoreManager: DatastoreManager
):ViewModel() {

    init {
        viewModelScope.launch {
            homeViewPreferenceLiveData = MutableLiveData(HOME_TYPE_LISTVIEW)
            homeViewPreferenceLiveData.value = datastoreManager.getHomeViewPreference()
        }
    }

    private val folderViewEventChannel = Channel<FolderViewEvent>()
    val folderViewEvent = folderViewEventChannel.receiveAsFlow()

    lateinit var homeViewPreferenceLiveData: MutableLiveData<Int>

    lateinit var folder: Folder

    var searchQuery = MutableLiveData<String>()

    var queryNotesFlow = searchQuery.asFlow().flatMapLatest {
        noteDao.getFolderNotesByQuery(it, folderID)
    }

    var queryNotesLiveData = queryNotesFlow.asLiveData()

    var folderID = 0
    set(value) {
        field = value
        folderContents = noteDao.getFolderContents(value).asLiveData()
    }

    var folderContents = noteDao.getFolderContents(0).asLiveData()

    var dataList = MutableLiveData<List<Note>>()

    fun folderContentsChanged(noteList: List<Note>){
        dataList.value = noteList
    }

    fun queryResultChanged(noteList: List<Note>){
        if (searchQuery.value == "") dataList.value = folderContents.value
        dataList.value = noteList
    }

    fun onDeleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
    }

    fun onPinnedNote(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
    }

    fun homeViewChanged(){
        viewModelScope.launch {
            when(homeViewPreferenceLiveData.value){
                HOME_TYPE_LISTVIEW -> {
                    homeViewPreferenceLiveData.value = HOME_TYPE_GRIDVIEW
                    datastoreManager.setHomeViewPreference(HOME_TYPE_GRIDVIEW)
                }
                HOME_TYPE_GRIDVIEW -> {
                    homeViewPreferenceLiveData.value = HOME_TYPE_LISTVIEW
                    datastoreManager.setHomeViewPreference(HOME_TYPE_LISTVIEW)
                }
            }
        }
    }
}

sealed class FolderViewEvent {
}