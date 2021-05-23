package com.cloudlevi.cloudnote.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.ITEM_TYPE_FOLDER
import com.cloudlevi.cloudnote.ITEM_TYPE_NOTE
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Folder
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.DialogDeleteConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteConfirmationDialog: DialogFragment(R.layout.dialog_delete_confirmation) {

    private lateinit var binding: DialogDeleteConfirmationBinding
    private lateinit var receivedItem: Any

    private var position = -1

    private var deleteItemBool: Boolean = false

    private lateinit var receivedNote: Note
    private lateinit var receivedFolder: Folder

    private var modifiedType: Int = -1

    private var dismissCalled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogDeleteConfirmationBinding.bind(view)

        binding.apply {
            if (arguments != null){
                if (DeleteConfirmationDialogArgs.fromBundle(requireArguments()).note != null) {
                    receivedNote = DeleteConfirmationDialogArgs.fromBundle(requireArguments()).note!!
                    modifiedType = ITEM_TYPE_NOTE
                }
                if (DeleteConfirmationDialogArgs.fromBundle(requireArguments()).folder != null) {
                    receivedFolder = DeleteConfirmationDialogArgs.fromBundle(requireArguments()).folder!!
                    modifiedType = ITEM_TYPE_FOLDER
                }

                position = DeleteConfirmationDialogArgs.fromBundle(requireArguments()).position

                var deleteItemString = ""

                if (modifiedType == ITEM_TYPE_NOTE) {
                    receivedItem = receivedNote
                    deleteItemString = "${getString(R.string.are_you_sure_you_want_to_delete)} '${receivedNote.title}'?"
                }
                else if (modifiedType == ITEM_TYPE_FOLDER) {
                    receivedItem = receivedFolder
                    deleteItemString = "${getString(R.string.are_you_sure_you_want_to_delete)} folder '${receivedFolder.title}' and all its contents?"
                }

                deleteItemTV.text = deleteItemString
            }

            noTV.setOnClickListener {
                deleteItemBool = false
                dismiss()
            }

            yesTV.setOnClickListener {
                deleteItemBool = true
                dismiss()
            }
        }
    }

    override fun dismiss() {
        if (!dismissCalled){
            super.dismiss()
            if (deleteItemBool) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("deleteItem", getModifiedItem())
            }
            else {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("position", position)
            }
            dismissCalled = true
        }
    }

    private fun getModifiedItem(): Any {
        return if (modifiedType == ITEM_TYPE_NOTE) receivedNote
        else receivedFolder
    }
}