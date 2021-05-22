package com.cloudlevi.cloudnote.ui.note

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.DialogChangePasswordBinding
import com.cloudlevi.cloudnote.databinding.DialogConfirmPinBinding
import com.cloudlevi.cloudnote.ui.main.PinConfirmDialogArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordDialog: DialogFragment(R.layout.dialog_change_password) {

    private lateinit var binding: DialogChangePasswordBinding
    private lateinit var receivedNote: Note

    private var dismissCalled = false

    private var passwordString = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogChangePasswordBinding.bind(view)

        binding.apply {
            if (arguments != null){
                receivedNote = ChangeFolderDialogArgs.fromBundle(requireArguments()).note
            }

            if (receivedNote.password.isEmpty()) {
                changePasswordTV.text = "Add a password"
                deleteButton.visibility = View.GONE
            }
            else changePasswordTV.text = "Change password"

            editTextNewPassword.addTextChangedListener {
                passwordString = it?.toString()?.trim() ?: ""
            }

            deleteButton.setOnClickListener {
                receivedNote.password = ""
                dismiss()
            }

            applyButton.setOnClickListener {
                if (passwordString.trim().length < 4)
                    Toast.makeText(requireContext(), "Password should be at least 4 characters long.", Toast.LENGTH_LONG).show()
                else {
                    receivedNote.password = passwordString
                    dismiss()
                }
            }
        }
    }

    override fun dismiss() {
        if (!dismissCalled){
            super.dismiss()
                findNavController().previousBackStackEntry?.savedStateHandle?.set("note_password", receivedNote)
            dismissCalled = true
        }
    }

}