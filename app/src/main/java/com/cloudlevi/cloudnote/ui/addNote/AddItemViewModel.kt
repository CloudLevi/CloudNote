package com.cloudlevi.cloudnote.ui.addNote

import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cloudlevi.cloudnote.ITEM_TYPE_FOLDER
import com.cloudlevi.cloudnote.ITEM_TYPE_NOTE
import com.cloudlevi.cloudnote.NAVIGATION_DESTINATION_FOLDER
import com.cloudlevi.cloudnote.NAVIGATION_DESTINATION_MAIN
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.data.NoteDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.cloudlevi.cloudnote.ui.addNote.AddItemEvent.*

class AddItemViewModel @ViewModelInject constructor(
    private val noteDao: NoteDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    var addFolderFeature = false
    var receivedFolderID = 0
    lateinit var receivedFolder: Folder

    var currentItemTypeChoice: Int = ITEM_TYPE_NOTE

    var chosenFolder = -1

    val folderTitlesHashMap = HashMap<Int, Pair<Int, String>>()

    var titleText = state.get<String>("titleText") ?: ""
        set(value) {
            field = value
            state.set("titleText", value)
        }

    var descriptionText = state.get<String>("descriptionText") ?: ""
        set(value) {
            field = value
            state.set("descriptionText", value)
        }

    private val foldersFlow = noteDao.getAllFolders()

    val foldersLiveData = foldersFlow.asLiveData()

    private val addItemEventChannel = Channel<AddItemEvent>()
    val addItemEvent = addItemEventChannel.receiveAsFlow()

    fun choiceClicked(clickedItemTypeChoice: Int) {
        if (currentItemTypeChoice != clickedItemTypeChoice) switchItemTypeChoice(
            clickedItemTypeChoice
        )
    }

    fun onAddButtonClicked() {
        changeProgressStatus(View.VISIBLE)

        if (addFolderFeature){
            when (currentItemTypeChoice) {
                ITEM_TYPE_NOTE -> {
                    if (titleText.isEmpty() || descriptionText.isEmpty()) sendToastMessage("Please fill in the empty fields!")
                    else insertNoteAndNavigate(
                        Note(
                            title = titleText,
                            description = descriptionText,
                            folder = chosenFolder
                        ),
                        NAVIGATION_DESTINATION_MAIN
                    )
                }
                ITEM_TYPE_FOLDER -> {
                    if (titleText.isEmpty()) sendToastMessage("Please fill in the empty fields!")
                    else insertFolderAndNavigate(Folder(title = titleText))
                }
            }
        }
        else if (titleText.isEmpty() || descriptionText.isEmpty()) sendToastMessage("Please fill in the empty fields!")
        else {
            insertNoteAndNavigate(
                Note(
                    title = titleText,
                    description = descriptionText,
                    folder = receivedFolderID
                ),
                NAVIGATION_DESTINATION_FOLDER
            )
        }
    }

    fun spinnerItemSelected(position: Int) {
        chosenFolder = folderTitlesHashMap[position]?.first ?: -1
    }

    fun getFolderTitles(foldersList: List<Folder>): ArrayList<String> {
        val arrayList = ArrayList<String>()
        folderTitlesHashMap.clear()
        arrayList.add("None")
        folderTitlesHashMap[0] = Pair(-1, "None")

        for ((index, folder) in foldersList.withIndex()) {
            arrayList.add(folder.title)
            folderTitlesHashMap[index + 1] = Pair(folder.id, folder.title)
        }
        return arrayList
    }

    private fun insertNoteAndNavigate(note: Note, navDestination: Int) = viewModelScope.launch {
        noteDao.insertNote(note)
        when(navDestination){
            NAVIGATION_DESTINATION_MAIN -> navigateToMainFragment()
            NAVIGATION_DESTINATION_FOLDER -> navigateToFolderFragment()
        }
    }

    private fun insertFolderAndNavigate(folder: Folder) = viewModelScope.launch {
        noteDao.insertFolder(folder)
        navigateToMainFragment()
    }

    private fun switchItemTypeChoice(newChoice: Int) = viewModelScope.launch {
        currentItemTypeChoice = newChoice
        addItemEventChannel.send(ChangeChoiceSelection(newChoice))
    }

    private fun sendToastMessage(message: String) = viewModelScope.launch {
        addItemEventChannel.send(SendToastMessage(message))
        changeProgressStatus(View.GONE)
    }

    private fun changeProgressStatus(status: Int) = viewModelScope.launch {
        addItemEventChannel.send(ChangeProgressStatus(status))
    }

    private fun navigateToMainFragment() = viewModelScope.launch {
        changeProgressStatus(View.GONE)
        addItemEventChannel.send(NavigateToMainFragment)
    }

    private fun navigateToFolderFragment() = viewModelScope.launch {
        changeProgressStatus(View.GONE)
        addItemEventChannel.send(NavigateToFolderFragment)
    }
}

sealed class AddItemEvent {
    data class ChangeChoiceSelection(val choice: Int) : AddItemEvent()
    data class SendToastMessage(val message: String) : AddItemEvent()
    data class ChangeProgressStatus(val status: Int) : AddItemEvent()
    object NavigateToMainFragment : AddItemEvent()
    object NavigateToFolderFragment : AddItemEvent()
}