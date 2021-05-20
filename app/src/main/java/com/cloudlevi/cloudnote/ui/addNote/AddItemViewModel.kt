package com.cloudlevi.cloudnote.ui.addNote

import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudlevi.cloudnote.ITEM_ADD_TYPE_FOLDER
import com.cloudlevi.cloudnote.ITEM_ADD_TYPE_NOTE
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
):ViewModel() {

    var currentItemTypeChoice: Int = ITEM_ADD_TYPE_NOTE

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


    private val addItemEventChannel = Channel<AddItemEvent>()
    val addItemEvent = addItemEventChannel.receiveAsFlow()

    fun choiceClicked(clickedItemTypeChoice: Int){
        if (currentItemTypeChoice != clickedItemTypeChoice) switchItemTypeChoice(clickedItemTypeChoice)
    }

    fun onAddButtonClicked(){
        changeProgressStatus(View.VISIBLE)

        when(currentItemTypeChoice){
            ITEM_ADD_TYPE_NOTE -> {
                if (titleText.isEmpty() || descriptionText.isEmpty()) sendToastMessage("Please fill in the empty fields!")
                else insertNote(Note(title = titleText, description = descriptionText))
            }
            ITEM_ADD_TYPE_FOLDER -> {
                if (titleText.isEmpty()) sendToastMessage("Please fill in the empty fields!")
                else insertFolder(Folder(title = titleText))
            }
        }
    }

    private fun insertNote(note: Note) = viewModelScope.launch {
        noteDao.insertNote(note)
        navigateToMainFragment()
    }

    private fun insertFolder(folder: Folder) = viewModelScope.launch {
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
}

sealed class AddItemEvent{
    data class ChangeChoiceSelection(val choice: Int): AddItemEvent()
    data class SendToastMessage(val message: String): AddItemEvent()
    data class ChangeProgressStatus(val status: Int): AddItemEvent()
    object NavigateToMainFragment: AddItemEvent()
}