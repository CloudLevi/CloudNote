package com.cloudlevi.cloudnote.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.DialogChangePasswordBinding
import com.cloudlevi.cloudnote.databinding.DialogEnterPasswordBinding
import com.cloudlevi.cloudnote.ui.note.ChangeFolderDialogArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterPasswordDialog: DialogFragment(R.layout.dialog_enter_password) {

    private lateinit var binding: DialogEnterPasswordBinding
    private lateinit var receivedNote: Note

    private var passwordString = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogEnterPasswordBinding.bind(view)

        binding.apply {
            if (arguments != null){
                receivedNote = EnterPasswordDialogArgs.fromBundle(requireArguments()).note
            }

            editTextPassword.addTextChangedListener {
                passwordString = it?.toString()?.trim() ?: ""
            }

            submitButton.setOnClickListener {
                when {
                    passwordString.trim().length < 4 -> Toast.makeText(requireContext(), "Password should be at least 4 characters long.", Toast.LENGTH_LONG).show()
                    passwordString.trim() != receivedNote.password -> {
                        Toast.makeText(requireContext(), "Incorrect password!", Toast.LENGTH_LONG).show()
                    }
                    else -> navigateToNoteFragment()
                }
            }
        }
    }

    private fun navigateToNoteFragment(){
        findNavController()
            .navigate(EnterPasswordDialogDirections.actionEnterPasswordDialogToNoteFragment(receivedNote))
    }

}