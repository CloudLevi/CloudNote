package com.cloudlevi.cloudnote.ui.note

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.databinding.DialogFolderChangeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_folder_change.*
import kotlinx.android.synthetic.main.fragment_add_item.*

@AndroidEntryPoint
class ChangeFolderDialog : DialogFragment(R.layout.dialog_folder_change) {

    private lateinit var binding: DialogFolderChangeBinding
    private val viewModel: ChangeFolderViewModel by viewModels()

    private var dismissCalled = false
    private var dismissCalledWithButton = false

    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (arguments != null) {
            viewModel.chosenFolder =
                ChangeFolderDialogArgs.fromBundle(requireArguments()).note.folder
            viewModel.currentNote = ChangeFolderDialogArgs.fromBundle(requireArguments()).note
        }


        binding = DialogFolderChangeBinding.bind(view)

        binding.apply {
            applyButton.setOnClickListener {
                viewModel.onApplyClicked()
                dismissCalledWithButton = true
                dismiss()
            }
        }


        viewModel.foldersLiveData.observe(viewLifecycleOwner) {
            spinnerAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.support_simple_spinner_dropdown_item,
                viewModel.getFolderTitles(it)
            )

            binding.folderChoiceSpinner.adapter = spinnerAdapter
            binding.folderChoiceSpinner.setSelection(viewModel.initialFolder)
        }

        folderChoiceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                viewModel.spinnerItemSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun dismiss() {
        if (!dismissCalled) {
            super.dismiss()
            if (dismissCalledWithButton) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    "chosen_folder",
                    viewModel.chosenFolder
                )
            }
            dismissCalled = true
        }
    }
}