package com.cloudlevi.cloudnote.ui.note

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.data.Note
import com.cloudlevi.cloudnote.databinding.DialogChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordDialog: DialogFragment(R.layout.dialog_change_password) {

    private lateinit var binding: DialogChangePasswordBinding
    private lateinit var receivedNote: Note

    private var dismissCalled = false

    private var passwordString = ""
    private var isCheckedTitle = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogChangePasswordBinding.bind(view)

        binding.apply {
            if (arguments != null){
                receivedNote = ChangePasswordDialogArgs.fromBundle(requireArguments()).note
            }

            isCheckedTitle = receivedNote.hideTitle

            hideTitleCheckBox.isChecked = isCheckedTitle

            hideTitleCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                isCheckedTitle = b
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
                receivedNote.hideTitle = false
                dismiss()
            }

            applyButton.setOnClickListener {
                if (passwordString.trim().length < 4)
                    Toast.makeText(requireContext(), "Password should be at least 4 characters long.", Toast.LENGTH_LONG).show()
                else {
                    receivedNote.password = passwordString
                    receivedNote.hideTitle = isCheckedTitle
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