package com.cloudlevi.cloudnote.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.databinding.DialogDeleteConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteConfirmationDialog: DialogFragment(R.layout.dialog_delete_confirmation) {

    private lateinit var binding: DialogDeleteConfirmationBinding
    private lateinit var receivedItem: Any

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogDeleteConfirmationBinding.bind(view)

        binding.apply {
            if (arguments != null){
                val receivedNote = DeleteConfirmationDialogArgs.fromBundle(requireArguments()).note
                val receivedFolder = DeleteConfirmationDialogArgs.fromBundle(requireArguments()).folder

                var deleteItemString = ""

                if (receivedNote != null) {
                    receivedItem = receivedNote
                    deleteItemString = "${getString(R.string.are_you_sure_you_want_to_delete)} ${receivedNote.title}?"
                }
                else if (receivedFolder != null) {
                    receivedItem = receivedFolder
                    deleteItemString = "${getString(R.string.are_you_sure_you_want_to_delete)} folder ${receivedFolder.title} and all its contents?"
                }

                deleteItemTV.text = deleteItemString
            }
        }
    }
}