package com.cloudlevi.cloudnote.ui.main

import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.DialogConfirmPinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinConfirmDialog: DialogFragment(R.layout.dialog_confirm_pin) {

    private lateinit var binding: DialogConfirmPinBinding
    private lateinit var receivedNote: Note

    private var position = -1

    private var answerBool: Boolean = false

    private var dismissCalled = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogConfirmPinBinding.bind(view)

        binding.apply {
            if (arguments != null){
                receivedNote = PinConfirmDialogArgs.fromBundle(requireArguments()).note
                position = PinConfirmDialogArgs.fromBundle(requireArguments()).position
            }

            if (receivedNote.pinned) pinItemTV.text = "Unpin Item?"
            else pinItemTV.text = "Pin Item?"

            noTV.setOnClickListener {
                answerBool = false
                dismiss()
            }

            yesTV.setOnClickListener {
                Log.d(TAG, "YES click listener before, ${receivedNote.pinned}")
                receivedNote.pinned = !receivedNote.pinned
                Log.d(TAG, "YES click listener after, ${receivedNote.pinned}")
                receivedNote.date_modified = System.currentTimeMillis()

                answerBool = true
                dismiss()
            }
        }
    }

    override fun dismiss() {
        if (!dismissCalled){
            super.dismiss()
            if (answerBool) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("note_pin", receivedNote)
            }
            else{
                findNavController().previousBackStackEntry?.savedStateHandle?.set("position", position)
            }
            dismissCalled = true
        }
    }
}