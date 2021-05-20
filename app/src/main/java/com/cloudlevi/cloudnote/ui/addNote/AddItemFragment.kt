package com.cloudlevi.cloudnote.ui.addNote

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cloudlevi.cloudnote.ITEM_ADD_TYPE_FOLDER
import com.cloudlevi.cloudnote.ITEM_ADD_TYPE_NOTE
import com.cloudlevi.cloudnote.R
import com.cloudlevi.cloudnote.ui.addNote.AddItemEvent.*
import com.cloudlevi.cloudnote.databinding.FragmentAddItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddItemFragment : Fragment(R.layout.fragment_add_item) {

    private lateinit var binding: FragmentAddItemBinding
    private val viewModel: AddItemViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddItemBinding.bind(view)

        binding.apply {
            choiceNoteButton.setOnClickListener {
                viewModel.choiceClicked(ITEM_ADD_TYPE_NOTE)
            }

            choiceFolderButton.setOnClickListener {
                viewModel.choiceClicked(ITEM_ADD_TYPE_FOLDER)
            }

            titleEditText.addTextChangedListener {
                viewModel.titleText = it.toString().trim()
            }

            descriptionEditText.addTextChangedListener {
                viewModel.descriptionText = it.toString().trim()
            }

            addButton.setOnClickListener {
                viewModel.onAddButtonClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addItemEvent.collect { event ->
                when (event) {
                    is ChangeProgressStatus -> changeProgressStatus(event.status)
                    is ChangeChoiceSelection -> changeChoiceSelection(event.choice)
                    is SendToastMessage -> sendToastMessage(event.message)
                    is NavigateToMainFragment -> findNavController()
                        .navigate(AddItemFragmentDirections.actionAddItemFragmentToMainFragment())
                }
            }
        }
    }

    private fun sendToastMessage(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    private fun changeProgressStatus(status: Int) {
        binding.progressBar.visibility = status
    }

    private fun changeChoiceSelection(newChoice: Int) {
        binding.apply {
            var stringAddButton = ""

            when (newChoice) {
                ITEM_ADD_TYPE_NOTE -> {
                    choiceNoteButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorActive
                        )
                    )
                    choiceFolderButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorInactive
                        )
                    )
                    descriptionEditText.visibility = View.VISIBLE
                    stringAddButton = "Add Note"
                }
                ITEM_ADD_TYPE_FOLDER -> {
                    choiceNoteButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorInactive
                        )
                    )
                    choiceFolderButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.buttonColorActive
                        )
                    )
                    descriptionEditText.visibility = View.GONE
                    stringAddButton = "Add Folder"
                }
            }
            addButton.text = stringAddButton
        }
    }
}